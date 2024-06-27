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
      alert('hello')
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
