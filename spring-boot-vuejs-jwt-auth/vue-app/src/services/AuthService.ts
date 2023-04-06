import apiClient from "@/utils/apiClient";
import router from "@/router";
import homeView from "@/views/HomeView.vue";
class AuthService {
  login = async (user: any): Promise<any> => {
    return await apiClient.post("/auth/login", {
      username: user.username,
      password: user.password,
    });
  };
  logout() {
    window.localStorage.removeItem("jwtToken");
    router.push("/login");
  }
}
export default new AuthService();
