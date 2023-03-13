<template>
  <div class="home">
    <h1>
      Welcome Back Admin ! <br />
      <br />
      Employees
    </h1>
    <div v-for="(employee, index) in employees" :key="index">
      <employee-card :employee="employee" />
    </div>
    <Footer></Footer>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";
import EmployeeCard from "@/components/EmployeeCard.vue";
import Footer from "@/components/FooterPage.vue";
import Employee from "@/types/Employee";
import employeeService from "@/services/employeeService";

export default defineComponent({
  name: "HomeView",
  components: { EmployeeCard, Footer },

  setup() {
    let employees = ref<Employee[]>([]);

    employeeService.getAllEmployees().then((response) => {
      employees.value = response;
    });

    return {
      employees,
    };
  },
});
</script>

<style scoped>
h1 {
  text-align: center;
  margin-bottom: 80 px;
}
</style>
