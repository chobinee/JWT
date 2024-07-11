<template>
	<div class="home">
		<HelloWorld msg="웹플랫폼2팀 로그인 과제 구현" />
		<h3>User 정보</h3>
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

export default {
	name: 'Home',
	components: {
		HelloWorld
	},
	data() {
		return {
			homeData: null
		}
	},
	created() {
		this.validateAccessToken();
	},
	methods: {
		//home api 호출
		getMyHome(accessToken) {
			return this.$axios.get('http://localhost:8080/home', {
				headers: {
					'Content-Type': `application/json`,
					'Authorization': `Bearer ${accessToken}`
				},
				withCredentials: true
			}).then((res) => {
				//response가 있을 경우
				// status가 200이면 data return
				if (res.status === 200) {
					return res.data;

				} else {
					//아닐 경우 에러처리
					swal({
						title: '500 Internal Server Error',
						text: '서버 오류입니다!',
						icon: 'error'
					});

				}
			}).catch((err) => {
				//error catch
				//status가 401일 경우
				if (err.response.status === 401) {
					//accessToken 만료, refresh 토큰 유효한 경우
					if (err.response.data.resultCode == 2) {
						//data에서 accessToken 가져와서 localStorage에 저장
						const newAccessToken = err.response.data.accessToken;
						localStorage.setItem('accessToken', newAccessToken);

					}
					else {
					// 이외 경우 에러처리
						swal({
							title: '500 Internal Server Error',
							text: '서버 오류입니다!',
							icon: 'error'
						});
					}
					throw err.response.data.resultCode;
				} else {
					//status가 401 아닐 경우 500 에러처리
					swal({
						title: '500 Internal Server Error',
						text: '서버 오류입니다!',
						icon: 'error'
					});

				}
			})
		},
		//accessToken 검증
		validateAccessToken() {
			const accessToken = localStorage.getItem('accessToken');
			if (!accessToken) {
			//localStorage에 accessToken이 없으면
				//로그인 필요 알림
				swal({
					title: '401 Unauthorized, NEED_LOGIN',
					text: '로그인이 필요합니다!',
					icon: 'error'
				});
				//root로 돌아감
				this.$router.push('/');

			} else {
				//accessToken이 저장되어 있으면
				//home api 호출
				this.getMyHome(accessToken)
					.then((data) => {
					//data 있을 경우 homeData에 저장
					this.homeData = data;
				}).catch((resultCode) => {
					//error일 경우
					if (resultCode == 2) {
					//accessToken 만료, refreshToken 유효 시
						//accessToken 재발급 알림
						swal({
							title: '401 Unauthorized, EXPIRED_ACC_TOKEN',
							text: 'Access token이 만료되어 Refresh token으로 재발급하였습니다.',
							icon: 'error'
						});

					} else if (resultCode == 3) {
						//accessToken 만료, refreshToken 만료 시
						//재로그인 알림
						swal({
							title: '401 Unauthorized, EXPIRED_REF_TOKEN',
							text: 'Refresh token이 만료되었습니다! 재로그인이 필요합니다.',
							icon: 'error'
						});
						//localStorage에 저장된 자원 삭제
						localStorage.removeItem('userId');
						localStorage.removeItem('isLoggedin');
						localStorage.removeItem('accessToken');

					} else {
						//resultCode가 2, 3이 아닐 경우
						//서버 에러
						swal({
							title: '500 Internal Server Error',
							text: '서버 오류입니다!',
							icon: 'error'
						});

					}
					this.$router.push('/');

				})

			}
		}
	}
}

</script>
