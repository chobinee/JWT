<template>
  <div class="home">
    <HelloWorld msg="웹플랫폼2팀 로그인 과제 구현"/>
  </div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from '@/components/HelloWorld.vue'

export default {
  name: 'HomeView',
  components: {
    HelloWorld
  }
}
</script>

<style>
.home {
  background-color:#0B0B3B
}
</style>
