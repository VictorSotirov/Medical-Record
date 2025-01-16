package com.example.medical_record.controllers;

import com.example.medical_record.DTOs.RoleDTO;
import com.example.medical_record.DTOs.UserDTO;
import com.example.medical_record.services.RoleService;
import com.example.medical_record.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
//@PreAuthorize("hasAuthority('ADMIN')") // Only allow ADMIN to access these endpoints
@AllArgsConstructor
public class AdminController
{
    private final UserService userService;

    private final RoleService roleService;

    // ----------------------------------------
    //  ROLE CRUD
    // ----------------------------------------


    @GetMapping
    public String getAdminPage()
    {
        return "admin/admin-page";
    }

    @GetMapping("/roles")
    public String listAllRoles(Model model)
    {
        List<RoleDTO> roles = this.roleService.getAllRoles();

        model.addAttribute("roles", roles);

        return "admin/roles";
    }

    /**
     * Show a form for creating a new role (GET).
     * Renders "admin/create-role.html"
     */
    @GetMapping("/roles/create")
    public String showCreateRoleForm(Model model)
    {
        model.addAttribute("roleForm", new RoleDTO());

        return "admin/create-role";
    }

    /**
     * Handle the creation of a new role (POST).
     * After creation, redirect back to /admin/roles
     */
    @PostMapping("/roles/create")
    public String createRole(@ModelAttribute("roleForm") @Valid RoleDTO roleDTO, BindingResult result
    ) {
        if (result.hasErrors())
        {
            // If validation fails, re-display the form with errors
            return "admin/create-role";
        }

        this.roleService.createRole(roleDTO);

        return "redirect:/admin/roles";
    }

    /**
     * Show a form to edit an existing role (GET).
     * Renders "admin/edit-role.html"
     */
    @GetMapping("/roles/edit/{id}")
    public String showEditRoleForm(@PathVariable Long id, Model model)
    {
        RoleDTO roleDTO = this.roleService.getRoleById(id);

        model.addAttribute("roleForm", roleDTO);

        model.addAttribute("roleId", id);

        return "admin/edit-role";
    }

    /**
     * Handle role edits (POST).
     * After update, redirect to /admin/roles
     */
    @PostMapping("/roles/edit/{id}")
    public String editRole(@PathVariable Long id, @ModelAttribute("roleForm") @Valid RoleDTO roleDTO, BindingResult result,
            Model model)
    {
        if (result.hasErrors())
        {
            model.addAttribute("roleId", id);

            return "admin/edit-role";
        }

        this.roleService.updateRole(id, roleDTO);

        return "redirect:/admin/roles";
    }

    /**
     * Delete a role (GET or POST).
     * After deletion, redirect to /admin/roles
     */
    @GetMapping("/roles/delete/{id}")
    public String deleteRole(@PathVariable Long id)
    {
        this.roleService.deleteRole(id);

        return "redirect:/admin/roles";
    }


    // ----------------------------------------
    //  USERS CRUD
    // ----------------------------------------

    /**
     * Display all users
     */
    @GetMapping("/users")
    public String listAllUsers(Model model)
    {
        List<UserDTO> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    /**
     * Show form to create a new user
     */
    @GetMapping("/users/create")
    public String showCreateUserForm(Model model)
    {
        UserDTO userForm = new UserDTO();

        userForm.setEnabled(true);

        model.addAttribute("userForm", userForm);

        return "admin/create-admin";
    }

    /**
     * Handle creating a new user (POST)
     */
    @PostMapping("/users/create")
    public String createUser(
            @ModelAttribute("userForm") @Valid UserDTO userDTO,
            BindingResult result,
            Model model
    )
    {
        if (result.hasErrors())
        {
            return "admin/create-admin";
        }

        userService.createAdmin(userDTO);

        return "redirect:/admin/users";
    }

    /**
     * Show form to edit an existing user
     */
    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model)
    {
        UserDTO userDTO = userService.getUserById(id);

        model.addAttribute("userForm", userDTO);

        model.addAttribute("userId", id);

        return "admin/edit-user";
    }

    /**
     * Handle editing a user (POST)
     */
    @PostMapping("/users/edit/{id}")
    public String editUser(
            @PathVariable Long id,
            @ModelAttribute("userForm") @Valid UserDTO userDTO,
            BindingResult result,
            Model model
    )
    {
        if (result.hasErrors())
        {
            model.addAttribute("userId", id);

            return "admin/edit-user";
        }

        userService.updateUser(id, userDTO);
        return "redirect:/admin/users";
    }

    /**
     * Delete user (GET or POST)
     */
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id)
    {
        this.userService.deleteUser(id);

        return "redirect:/admin/users";
    }
}
