<template>
  <div class="home">
    <HelloWorld msg="웹플랫폼2팀 로그인 과제 구현"/>
    <h3>유저 정보</h3>
    <div v-if="homeData">
      <p>ID: {{ homeData.id }}</p>
      <p>PW: {{ homeData.pw }}</p>
      <p>Name: {{ homeData.name }}</p>
      <p>IsAdmin: {{ homeData.admin }}</p>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from '@/components/HelloWorld.vue'
import axios from 'axios'

export default {
  name: 'Home',
  components: {
    HelloWorld
  },
  data() {
    return {
      homeData: null
    };
  },
  created() {
    this.fetchHomeData();
  },
  methods: {
    fetchHomeData() {
      const accessToken = localStorage.getItem('accessToken');
      if (!accessToken) {
        alert("로그인이 필요합니다!");
        this.$router.push('/');
      } else {
        getMyHome(accessToken).then(data => {
          this.homeData = data;
        }).catch(error => {
          if (error == 2) alert("accessToken 만료! refreshToken으로 재발급하였습니다.")
          else if (error == 3){
            alert("refreshToken 만료! 재로그인하세요.");
            localStorage.removeItem("userId");
            localStorage.removeItem("isLoggedin");
            localStorage.removeItem("accessToken");
          }
          this.$router.push('/');
        });
      }
    }
  }
}

export function getMyHome(accessToken) {
    return axios.get('http://localhost:8080/home', {
      headers: {
        'Content-Type': `application/json`,
        'Authorization' : `Bearer ${accessToken}`,
      },
      withCredentials: true
    }).then((res) => {
      console.log(res);
      if (res.status === 200)
        return res.data;
    }).catch((err) => {if (err.response.status === 401)
    {
      if (err.response.data.resultCode == 2)
      {
        const newAccessToken = err.response.data.accessToken;
        localStorage.setItem('accessToken', newAccessToken);
      }
      throw err.response.data.resultCode;
    }});
}

</script>
