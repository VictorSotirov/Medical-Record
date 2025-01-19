package com.example.medical_record.controllers;

import com.example.medical_record.DTOs.RoleDTO;
import com.example.medical_record.DTOs.UserDTO;
import com.example.medical_record.services.RoleService;
import com.example.medical_record.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AdminController
{
    private final UserService userService;

    private final RoleService roleService;


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

    @GetMapping("/roles/create")
    public String showCreateRoleForm(Model model)
    {
        model.addAttribute("roleForm", new RoleDTO());

        return "admin/create-role";
    }

    @PostMapping("/roles/create")
    public String createRole(@ModelAttribute("roleForm") @Valid RoleDTO roleDTO, BindingResult result
    ) {
        if (result.hasErrors())
        {
            return "admin/create-role";
        }

        this.roleService.createRole(roleDTO);

        return "redirect:/admin/roles";
    }

    @GetMapping("/roles/edit/{id}")
    public String showEditRoleForm(@PathVariable Long id, Model model)
    {
        RoleDTO roleDTO = this.roleService.getRoleById(id);

        model.addAttribute("roleForm", roleDTO);

        model.addAttribute("roleId", id);

        return "admin/edit-role";
    }

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

    @GetMapping("/roles/delete/{id}")
    public String deleteRole(@PathVariable Long id)
    {
        this.roleService.deleteRole(id);

        return "redirect:/admin/roles";
    }

    @GetMapping("/users")
    public String listAllUsers(Model model)
    {
        List<UserDTO> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/users/create")
    public String showCreateUserForm(Model model)
    {
        UserDTO userForm = new UserDTO();

        userForm.setEnabled(true);

        model.addAttribute("userForm", userForm);

        return "admin/create-admin";
    }

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

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model)
    {
        UserDTO userDTO = userService.getUserById(id);

        model.addAttribute("userForm", userDTO);

        model.addAttribute("userId", id);

        return "admin/edit-user";
    }

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

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id)
    {
        this.userService.deleteUser(id);

        return "redirect:/admin/users";
    }
}
