<template>
  <div class="card">
    <div class="card-header">
      <div class="card-header-left">
        <h2 class="card-title">{{ employee.name }}</h2>
      </div>
      <div class="card-header-right">
        <button class="btn" @click="editEmployee">Edit</button>
        <button class="btn" @click="deleteEmployee">Delete</button>
        <button class="btn" @click="showDetails">Details</button>
      </div>
    </div>
    <div class="card-body">
      <div class="card-row">
        <div class="card-label">Phone:</div>
        <div class="card-value">{{ employee.phone }}</div>
      </div>
      <div class="card-row">
        <div class="card-label">Email:</div>
        <div class="card-value">{{ employee.email }}</div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import employeeService from "@/services/employeeService";
import Employee from "@/types/Employee";
import { defineComponent, PropType } from "vue";
import { useRouter } from "vue-router";

export default defineComponent({
  name: "EmployeeCard",
  props: {
    employee: {
      type: Object as PropType<Employee>,
      required: true,
    },
  },
  setup(props) {
    const router = useRouter();
    const showDetails = () => {
      router.push({ name: "details", params: { id: props.employee.id } });
    };

    const editEmployee = () => {
      router.push({
        name: "edit",
        params: { id: props.employee.id },
      });
    };
    const deleteEmployee = () => {
      employeeService.delete(props.employee.id).then(() => {
        window.location.reload;
      });
    };

    return {
      showDetails,
      editEmployee,
      deleteEmployee,
    };
  },
});
</script>
<style>
.card {
  background-color: #f9fafb;
  border-radius: 8px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  margin: 16px;
  overflow: hidden;
}

.card-header {
  background-color: #1890ff;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
}

.card-title {
  margin: 0;
  font-size: 24px;
}

.card-subtitle {
  margin: 0;
  font-size: 14px;
  opacity: 0.8;
}

.card-header-right {
  display: flex;
  align-items: center;
}

.btn {
  background-color: #fff;
  border: 1px solid #1890ff;
  border-radius: 4px;
  color: #1890ff;
  font-size: 14px;
  margin-left: 8px;
  padding: 8px 16px;
}

.btn:hover {
  background-color: #1890ff;
  color: #fff;
}

.card-body {
  padding: 16px;
}

.card-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.card-label {
  font-size: 14px;
  font-weight: bold;
  width: 100px;
}

.card-value {
  font-size: 14px;
}
</style>
