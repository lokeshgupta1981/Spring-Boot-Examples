<template>
  <div class="employee-edit-form">
    <form @submit.prevent="submitForm">
      <div class="form-group">
        <label for="name">Name</label>
        <input
          type="text"
          id="name"
          v-model="employeeToBeSaved.name"
          required
        />
      </div>
      <div class="form-group">
        <label for="email">Email</label>
        <input
          type="email"
          id="email"
          v-model="employeeToBeSaved.email"
          required
        />
      </div>
      <div class="form-group">
        <label for="phone">Phone</label>
        <input
          type="tel"
          id="phone"
          v-model="employeeToBeSaved.phone"
          required
        />
      </div>
      <div class="form-group">
        <label for="position">Position</label>
        <input
          type="text"
          id="position"
          v-model="employeeToBeSaved.position"
          required
        />
      </div>
      <div class="form-group">
        <label for="bio">Bio</label>
        <textarea id="bio" v-model="employeeToBeSaved.bio" required></textarea>
      </div>
      <div class="form-actions">
        <button type="submit">Save Employee</button>
        <button type="button" @click="cancelEdit">Cancel</button>
      </div>
    </form>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";
import Employee from "@/types/Employee";
import employeeService from "@/services/employeeService";
import { useRouter } from "vue-router";

export default defineComponent({
  name: "AddEmployeeView",
  setup() {
    const employeeToBeSaved = ref<Employee>({
      id: 0,
      name: "",
      phone: "",
      email: "",
      bio: "",
      position: "",
    });
    const router = useRouter();

    const submitForm = () => {
      employeeService.save(employeeToBeSaved.value).then(() => {
        router.push({ name: "home" });
      });
      window.location.reload;
    };
    const cancelEdit = () => {
      router.push({ name: "home" });
    };

    return {
      employeeToBeSaved,
      cancelEdit,
      submitForm,
    };
  },
});
</script>

<style scoped>
.employee-edit-form {
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  padding: 2rem;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  font-size: 1.2rem;
  margin-bottom: 0.5rem;
}

input,
textarea {
  border-radius: 3px;
  border: 1px solid #ccc;
  padding: 0.5rem;
  font-size: 1.2rem;
  width: 100%;
  box-sizing: border-box;
}

button {
  background-color: #007aff;
  color: #fff;
  border: none;
  border-radius: 3px;
  padding: 0.5rem 1rem;
  font-size: 1.2rem;
  cursor: pointer;
  margin-right: 1rem;
}

button:hover {
  background-color: #0062cc;
}

button[type="submit"] {
  background-color: #28a745;
}

button[type="submit"]:hover {
  background-color: #218838;
}

button[type="button"] {
  background-color: #dc3545;
}

button[type="button"]:hover {
  background-color: #c82333;
}

.form-actions {
  margin-top: 1rem;
  display: flex;
  justify-content: flex-end;
}
</style>
