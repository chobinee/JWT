<template>
  <div class="login">
    <h1>Login</h1>
    <div id="loginForm">
      <form @submit.prevent="fnLogin" v-if="!isLoggedin">
        <p>
          <input name="uid" placeholder="Enter your ID" v-model="userId"><br>
        </p>
        <p>
          <input name="password" type="password" placeholder="Enter your password" v-model="userPw"><br>
        </p>
        <p>
          <button type="submit">Login</button>
        </p>
      </form>
      <div v-else>
        <p>Welcome, {{ userId }}!</p>
        <p>
          <button @click="fnLogout">Logout</button>
        </p>
      </div>
    </div>
  </div>
</template>
<script>


import axios from 'axios'

export default {
  data () {
    return {
      userId: '',
      userPw: '',
      isLoggedin: localStorage.getItem("isLoggedin")
    }
  },
  created () {
    if (this.isLoggedin)
      this.userId = localStorage.getItem("userId");
  },
  methods: {
    fnLogin () {
      if (this.userId === '' || this.userPw === '') {
        swal({title: 'check your input!', text: 'ID와 PW를 입력해주세요!', icon: 'error'});
        return
      }
      let body = {};
      body.id = this.userId;
      body.pw = this.userPw;

      try {
        axios.post('http://localhost:8080/login', JSON.stringify(body), {
          headers: {
            "Content-Type": `application/json`,
          },
          withCredentials: true
        }).then((res) => {
          console.log(res);
          if (res.status === 200)
          {
            swal({title: '200 OK', text: '로그인 성공', icon: 'success'});
            this.isLoggedin = true;
            localStorage.setItem("isLoggedin", true);
            localStorage.setItem("userId", this.userId);
            localStorage.setItem("accessToken", res.data);
          }
          else if (res.status === 204)
          {
            swal({title: '204 No Content', text: '존재하지 않는 유저입니다.', icon: 'success'});
          }
        }).catch((err) => {
          if (err.response.status === 400)
            swal({title: '400 Bad Request', text: err.response.data, icon: 'error'})});
      }
      catch(error) {
        console.error(error);
      }
    },
    fnLogout() {
      swal({title: 'OK', text: '로그아웃 성공', icon: 'success'});
      localStorage.removeItem("accessToken");
      localStorage.removeItem("isLoggedin");
      localStorage.removeItem("userId");
      this.isLoggedin = false;
      this.userId = '';
      this.userPw = '';
    }
  }
}
</script>

<style>
#loginForm {
  width: 500px;
  margin: auto;
  align-content: center;
}
</style>
