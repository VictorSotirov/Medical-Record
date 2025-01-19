package com.example.medical_record.controllers;

import com.example.medical_record.DTOs.RoleDTO;
import com.example.medical_record.DTOs.UserDTO;
import com.example.medical_record.services.RoleService;
import com.example.medical_record.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@WithMockUser(roles = "ADMIN")  // All tests run as an authenticated ADMIN
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @Test
    @DisplayName("GET /admin -> returns 'admin/admin-page'")
    void getAdminPage_shouldReturnAdminPage() throws Exception
    {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-page"));
    }
    @Test
    @DisplayName("GET /admin/roles -> returns 'admin/roles' with list of roles")
    void listAllRoles_shouldReturnRolesViewAndModel() throws Exception
    {
        List<RoleDTO> mockRoles = List.of(new RoleDTO(1L, "ROLE_USER"), new RoleDTO(2L, "ROLE_ADMIN"));
        when(roleService.getAllRoles()).thenReturn(mockRoles);

        mockMvc.perform(get("/admin/roles"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/roles"))
                .andExpect(model().attribute("roles", mockRoles));
    }

    @Test
    @DisplayName("GET /admin/roles/create -> returns 'admin/create-role' with roleForm")
    void showCreateRoleForm_shouldReturnCreateRoleView() throws Exception
    {
        mockMvc.perform(get("/admin/roles/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/create-role"))
                .andExpect(model().attributeExists("roleForm"));
    }

    @Test
    @DisplayName("POST /admin/roles/create -> redirects to /admin/roles on success")
    void createRole_shouldRedirectWhenValid() throws Exception
    {
        doNothing().when(roleService).createRole(any(RoleDTO.class));

        mockMvc.perform(post("/admin/roles/create")
                        .param("authority", "ROLE_NEW")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/roles"));

        verify(roleService).createRole(any(RoleDTO.class));
    }

    @Test
    @DisplayName("POST /admin/roles/create -> returns form if validation fails (example: blank name)")
    void createRole_shouldReturnFormWhenValidationFails() throws Exception
    {
        mockMvc.perform(post("/admin/roles/create")
                        .param("authority", "")  // blank => triggers validation error if @NotBlank
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/create-role"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("roleForm", "authority"));
    }

    @Test
    @DisplayName("GET /admin/roles/edit/{id} -> returns 'admin/edit-role' with roleForm")
    void showEditRoleForm_shouldReturnEditView() throws Exception {
        RoleDTO mockRole = new RoleDTO(1L, "ROLE_OLD");
        when(roleService.getRoleById(1L)).thenReturn(mockRole);

        mockMvc.perform(get("/admin/roles/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit-role"))
                .andExpect(model().attribute("roleForm", mockRole))
                .andExpect(model().attribute("roleId", 1L));
    }

    @Test
    @DisplayName("POST /admin/roles/edit/{id} -> redirects to /admin/roles on success")
    void editRole_shouldRedirectWhenValid() throws Exception {
        doNothing().when(roleService).updateRole(eq(1L), any(RoleDTO.class));

        mockMvc.perform(post("/admin/roles/edit/1")
                        .with(csrf())
                        .param("authority", "ROLE_UPDATED")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/roles"));

        verify(roleService).updateRole(eq(1L), any(RoleDTO.class));
    }

    @Test
    @DisplayName("POST /admin/roles/edit/{id} -> returns form if validation fails")
    void editRole_shouldReturnFormWhenValidationFails() throws Exception {
        mockMvc.perform(post("/admin/roles/edit/1")
                        .with(csrf())
                        .param("authority", "")  // blank => validation error
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit-role"))
                .andExpect(model().attributeHasFieldErrors("roleForm", "authority"))
                .andExpect(model().attribute("roleId", 1L));
    }

    @Test
    @DisplayName("GET /admin/roles/delete/{id} -> redirects to /admin/roles")
    void deleteRole_shouldRedirectToRoles() throws Exception {
        doNothing().when(roleService).deleteRole(1L);

        mockMvc.perform(get("/admin/roles/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/roles"));
    }

    @Test
    @DisplayName("GET /admin/users -> returns 'admin/users' with list of users")
    void listAllUsers_shouldReturnUsersViewAndModel() throws Exception
    {
        List<UserDTO> mockUsers = List.of(
                new UserDTO(1L, "user1", "pass1", true, null),
                new UserDTO(2L, "user2", "pass2", true, null)
        );
        when(userService.getAllUsers()).thenReturn(mockUsers);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("users", mockUsers));
    }

    @Test
    @DisplayName("GET /admin/users/create -> returns 'admin/create-admin' with userForm")
    void showCreateUserForm_shouldReturnCreateUserView() throws Exception
    {
        mockMvc.perform(get("/admin/users/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/create-admin"))
                .andExpect(model().attributeExists("userForm"));
    }

    @Test
    @DisplayName("POST /admin/users/create -> redirects to /admin/users on success")
    void createUser_shouldRedirectOnSuccess() throws Exception
    {
        doNothing().when(userService).createAdmin(any(UserDTO.class));

        mockMvc.perform(post("/admin/users/create")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("password", "secret123")
                        .param("enabled", "true")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).createAdmin(any(UserDTO.class));
    }

    @Test
    @DisplayName("POST /admin/users/create -> returns form if validation fails (e.g. blank fields)")
    void createUser_shouldReturnFormWhenValidationFails() throws Exception
    {
        mockMvc.perform(post("/admin/users/create")
                        .with(csrf())
                        .param("username", "")
                        .param("password", "somepass")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/create-admin"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("userForm", "username"));
    }

    @Test
    @DisplayName("GET /admin/users/edit/{id} -> returns 'admin/edit-user' with userForm")
    void showEditUserForm_shouldReturnEditView() throws Exception
    {
        UserDTO mockUser = new UserDTO(1L, "existingUser", null, true, null);
        when(userService.getUserById(1L)).thenReturn(mockUser);

        mockMvc.perform(get("/admin/users/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit-user"))
                .andExpect(model().attribute("userForm", mockUser))
                .andExpect(model().attribute("userId", 1L));
    }

    @Test
    @DisplayName("POST /admin/users/edit/{id} -> redirects to /admin/users on success")
    void editUser_shouldRedirectOnSuccess() throws Exception
    {
        doNothing().when(userService).updateUser(eq(1L), any(UserDTO.class));

        mockMvc.perform(post("/admin/users/edit/1")
                        .with(csrf())
                        .param("username", "updatedUser")
                        .param("password", "newPass")
                        .param("enabled", "true")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).updateUser(eq(1L), any(UserDTO.class));
    }

    @Test
    @DisplayName("POST /admin/users/edit/{id} -> returns form if validation fails")
    void editUser_shouldReturnFormWhenValidationFails() throws Exception
    {
        mockMvc.perform(post("/admin/users/edit/1")
                        .with(csrf())
                        .param("username", "")
                        .param("password", "somePassword")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit-user"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("userForm", "username"))
                .andExpect(model().attribute("userId", 1L));
    }

    @Test
    @DisplayName("GET /admin/users/delete/{id} -> redirects to /admin/users after deletion")
    void deleteUser_shouldRedirectOnSuccess() throws Exception
    {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(get("/admin/users/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).deleteUser(1L);
    }
}