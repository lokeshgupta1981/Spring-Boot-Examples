import HomeView from "@/views/HomeView.vue";
import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import ShowDetails from "@/views/ShowDetailsView.vue";
import EditEmployeeView from "@/views/EditEmployeeView.vue";
import AddEmployeeView from "@/views/AddEmployeeView.vue";
import AboutView from "@/views/AboutView.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "home",
    component: HomeView,
  },
  {
    path: "/about",
    name: "about",
    component: AboutView,
    meta: {
      requiresAuth: true,
    },
  },
  {
    path: "/edit/employee/:id",
    name: "edit",
    component: EditEmployeeView,
    props: true,
  },
  {
    path: "/add/employee",
    name: "add",
    component: AddEmployeeView,
    props: true,
  },
  {
    path: "/details/employee/:id",
    name: "details",
    component: ShowDetails,
  },
];

const router = createRouter({
  routes,
  history: createWebHistory(process.env.BASE_URL),
});

export default router;
