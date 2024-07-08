<template>
  <div class="hello">
    <img alt="My code" src="../assets/myCode.png">
    <h1>{{ msg }}</h1>
    <a href="https://periwinkle-bedbug-640.notion.site/JWT-8ba1d60d1b474ce18890db2a1f6cbeb9?pvs=74" target="_blank">이론 정리 요기</a>
  </div>
</template>

<script>
export default {
  name: 'HelloWorld',
  props: {
    msg: String
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.hello {
  color: #FFFFFF
}
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
