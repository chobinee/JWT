<template>
	<div id="app">
		<div id="nav">
			<router-link to="/">Login</router-link>
			|
			<router-link to="/home">Home</router-link>
		</div>
		<router-view />
	</div>
</template>

<style>
#app {
	font-family: Avenir, Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
	text-align: center;
	color: #FFFFFF;
}

#nav {
	padding: 30px;
}

#nav a {
	font-weight: bold;
	color: #CEECF5;
}

#nav a.router-link-exact-active {
	color: #FFBF00;
}
</style>
