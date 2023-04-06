<template>
  <div>
    <input type="text" v-model="user.username" placeholder="Username" />
    <input type="password" v-model="user.password" placeholder="Password" />
    <button @click="login">Login</button>
  </div>
</template>

<script>
import AuthService from "@/services/AuthService";
import router from "@/router";
export default {
  data() {
    return {
      user: {
        username: "",
        password: "",
      },
    };
  },
  methods: {
    login() {
      AuthService.login(this.user).then((response) => {
        if (response.data.accessToken) {
          window.localStorage.clear();
          window.localStorage.setItem("jwtToken", response.data.accessToken);
        }
        window.location.href = "/";
      });
    },
  },
};
</script>

<style scoped>
div {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}

input {
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 5px;
  border: none;
  box-shadow: 0px 0px 5px 0px rgba(0, 0, 0, 0.2);
}

button {
  padding: 10px;
  background-color: #2196f3;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s ease;
}

button:hover {
  background-color: #0d8bf2;
}
</style>
