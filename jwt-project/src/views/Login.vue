<template>
  <div class="login">
    <h1>Login</h1>
    <div id="loginForm">
      <form @submit.prevent="fnLogin">
        <p>
          <input name="uid" placeholder="Enter your ID" v-model="user_id"><br>
        </p>
        <p>
          <input name="password" type="password" placeholder="Enter your password" v-model="user_pw"><br>
        </p>
        <p>
          <button type="submit">Login</button>
        </p>
      </form>
    </div>
  </div>
</template>
<script>


import axios from 'axios'

export default {
  data () {
    return {
      user_id: '',
      user_pw: ''
    }
  },
  methods: {
    fnLogin () {
      if (this.user_id === '' || this.user_pw === '') {
        alert('Check your input.')
        return
      }
      let body = {};
      body.id = this.user_id;
      body.pw = this.user_pw;

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
            alert("로그인 성공!");
            localStorage.setItem("accessToken", res.data);
          }
          else if (res.status === 204)
          {
            alert("없는 유저입니다!");
          }
        }).catch((err) => {if (err.response.status === 400) alert(err.response.data);});
      }
      catch(error) {
        console.error(error);
      }
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
