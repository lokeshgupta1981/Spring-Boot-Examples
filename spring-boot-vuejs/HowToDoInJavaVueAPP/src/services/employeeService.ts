import apiClient from "@/utils/apiClient";
import Employee from "@/types/Employee";

class EmployeeService {
  // get All Employees
  getAllEmployees = (): Promise<Employee[]> => {
    return apiClient.get("/employees").then((response) => response.data);
  };
  getEmployeeById = (id: any): Promise<Employee> => {
    return apiClient.get(`/employees/${id}`).then((response) => response.data);
  };

  edit = (id: any, emp: any): Promise<Employee> => {
    return apiClient
      .put(`/employees/${id}`, emp)
      .then((response) => response.data);
  };
  delete = async (id: any): Promise<Employee> => {
    return await apiClient
      .delete(`/employees/${id}`)
      .then((response) => response.data);
  };
  save = (emp: any): Promise<Employee> => {
    return apiClient.post("/employees", emp).then((response) => response.data);
  };
}

export default new EmployeeService();
