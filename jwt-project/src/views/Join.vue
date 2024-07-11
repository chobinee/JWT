<template>
	<form @submit.prevent="fnJoin">
		<div>
			<label>id</label>
			<input v-model="userId" type="text"/>
		</div>
		<div>
			<label>비밀번호</label>
			<input v-model="userPw" type="password"/>
		</div>
		<div>
			<label>비밀번호 확인</label>
			<input v-model="pwConfirm" type="password"/>
		</div>
		<div>
			<label>이름</label>
			<input v-model="userName" type="text"/>
		</div>
		<button type="submit">회원가입</button>
	</form>
</template>

<script>

export default {
	data() {
		return {
			userId: '',
			userPw: '',
			userName: '',
			pwConfirm: ''
		}
	},
	methods: {
		fnJoin() {
			//정보가 모두 기입되지 않았을 경우
			if (this.userId === '' || this.userPw === '' || this.userName === '') {
				swal({
					title: 'check your input!',
					text: '정보를 모두 입력해주세요!',
					icon: 'error'
				});
				return;

			}
			//비밀번호와 비밀번호 확인란 입력이 다를 경우
			if (this.pwConfirm !== this.userPw) {
				swal({
					title: 'check your password!',
					text: '비밀번호와 비밀번호 확인란 입력이 다릅니다!',
					icon: 'error'
				});
				return;

			}
			//request body 선언
			let body = {
				id : this.userId,
				pw: this.userPw,
				name: this.userName,
				admin: false
			};
			try {
				//join api 호출
				this.$axios.post('http://localhost:8080/join', JSON.stringify(body), {
					headers: {
						'Content-Type': `application/json`
					},
					withCredentials: true
				}).then((res) => {
					// 회원가입 성공 시
					if (res.status === 201) {
						swal({
							title: '201 Created',
							text: '회원가입 성공',
							icon: 'success'
						});
						this.$router.push('/');

					}
					// 회원가입 실패 시
				}).catch((err) => {
					swal({
						title: 'Error!',
						text: err.response.data,
						icon: 'error'
					});

				})
				//join api 호출 실패 시
			} catch (error) {
				console.error(error);

			}
		}
	}
}

</script>