<template>
	<div class="login">
		<h1>Login</h1>
		<div id="loginForm">
			<form
				v-if="!isLoggedin"
				@submit.prevent="fnLogin"
			>
				<p>
					<input v-model="userId" placeholder="Enter your ID"><br>
				</p>
				<p>
					<input v-model="userPw" type="password" placeholder="Enter your password"><br>
				</p>
				<p>
					<button type="submit">Login</button>
					<button @click=join()>Join</button>
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

export default {
	data() {
		return {
			userId: '',
			userPw: '',
			isLoggedin: localStorage.getItem('isLoggedin')
		}
	},
	created() {
		//로그인 되어있는지 확인 후 userId 가져옴
		if (this.isLoggedin) {
			this.userId = localStorage.getItem('userId');

		}
	},
	methods: {
		//로그인
		fnLogin() {
			//id, pw가 둘다 입력되지 않았을 때
			if (this.userId === '' || this.userPw === '') {
				swal({
					title: 'check your input!',
					text: 'ID와 PW를 입력해주세요!',
					icon: 'error'
				});
				return;

			}
			//request body 선언
			let body = {
				id: this.userId,
				pw: this.userPw
			};
			try {
				//login api 호출
				this.$axios.post('http://localhost:8080/login', JSON.stringify(body), {
					headers: {
						'Content-Type': `application/json`
					},
					withCredentials: true
				}).then((res) => {
					//response status가 200일 시
					if (res.status === 200) {
						swal({
							title: '200 OK',
							text: '로그인 성공',
							icon: 'success'
						});
						//localStorage에 유저 정보 저장
						this.isLoggedin = true;
						localStorage.setItem('isLoggedin', true);
						localStorage.setItem('userId', this.userId);
						localStorage.setItem('accessToken', res.data);

					} else if (res.status === 204) {
						//response status가 204일 시
						//존재하지 않는 유저 알림
						swal({
							title: '204 No Content',
							text: '존재하지 않는 유저입니다.',
							icon: 'success'
						});

					} else {
						//이외의 경우 에러처리
						swal({
							title: '500 Internal Server Error',
							text: '서버 오류입니다!',
							icon: 'error'
						});

					}
				}).catch((err) => {
					//response가 error일 시
					if (err.response.status === 400) {
						//error status가 400일 시
						//bad request 알림
						swal({
							title: '400 Bad Request',
							text: err.response.data,
							icon: 'error'
						});

					} else {
						//400 아닐 경우 에러처리
						swal({
							title: '500 Internal Server Error',
							text: '서버 오류입니다!',
							icon: 'error'
						});

					}

				});
				//login 요청 실패 시 error
			} catch (error) {
				console.error(error);

			}
		},
		//로그아웃
		fnLogout() {
			swal({
				title: 'OK',
				text: '로그아웃 성공',
				icon: 'success'
			});
			//localStorage에 저장된 유저 정보 삭제
			localStorage.removeItem('accessToken');
			localStorage.removeItem('isLoggedin');
			localStorage.removeItem('userId');
			this.isLoggedin = false;
			this.userId = '';
			this.userPw = '';
		},
		//회원가입 라우터로 이동
		join() {
			this.$router.push('/join');
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
