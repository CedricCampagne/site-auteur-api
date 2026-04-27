package com.cedric.site_auteur_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cedric.site_auteur_api.dto.auth.LoginDto;
import com.cedric.site_auteur_api.dto.auth.RegisterDto;
import com.cedric.site_auteur_api.dto.user.UserCreateDto;
import com.cedric.site_auteur_api.dto.user.UserUpdateDto;
import com.cedric.site_auteur_api.dto.user.UserFullDto;

import com.cedric.site_auteur_api.entity.User;
import com.cedric.site_auteur_api.entity.UserRole;
import com.cedric.site_auteur_api.entity.Role;
import com.cedric.site_auteur_api.mapper.UserMapper;
import com.cedric.site_auteur_api.repository.RoleRepository;
import com.cedric.site_auteur_api.repository.UserRepository;
import com.cedric.site_auteur_api.repository.UserRoleRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    
    //injecte les repository
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    // injecte l'encoder
    private final PasswordEncoder passwordEncoder;
    //Constructeur
    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        RoleRepository roleRepository,
        UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    //All
    public List<UserFullDto> getAllUsers() {
        return userRepository.findAll()
            .stream()
            .map(UserMapper::toFullDto)
            .toList();
    }
    // by id
    public UserFullDto getUserById(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Utilisateur non trouvable avecl 'id : " + id));

        return UserMapper.toFullDto(user);
    }

    // Create
    public UserFullDto createUser(UserCreateDto dto) {
        User user = new User();

        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password()); // plus tard → BCrypt
        user.setIsActive(true);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        User saved = userRepository.save(user);

        return UserMapper.toFullDto(saved);
    }

    //Update
    public UserFullDto updateUser(Integer id,UserUpdateDto dto) {
        User user = userRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Utilisateur introuvable avec l'id : " + id));
        
        user.setUsername(dto.username());
        user.setEmail(dto.email()); 
        user.setUpdatedAt(OffsetDateTime.now());

        User updated = userRepository.save(user);

        return UserMapper.toFullDto(updated);
    }

    //Delete
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Utilisateur introuvable avec l 'id : " + id));

        userRepository.delete(user);
    }

    //Toggle status
    public UserFullDto toggleUser(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable avec l'id : " + id));

        user.setIsActive(!user.getIsActive());
        user.setUpdatedAt(OffsetDateTime.now());

        User updated = userRepository.save(user);

        return UserMapper.toFullDto(updated);
    }

    // Register
    public UserFullDto register (RegisterDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // Recupère le role "user" via methode custop RoleRepository
        Role roleUser = roleRepository.findByRoleName(("user"))
            .orElseThrow(()-> new RuntimeException("Role USER introuvable"));

        // Créer un nouvel utilisateur
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setIsActive(true);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        // Sauvegarder l’utilisateur
        User saved = userRepository.save(user);

        // On crée la relation entre : l’utilisateur qu’on vient de créer le rôle USER récupéré plus haut
        UserRole userRole = new UserRole();
        // Remplir l'ID composite
        userRole.getId().setUserId(saved.getIdUser());
        userRole.getId().setRoleId(roleUser.getIdRole());
        // Relations
        userRole.setUser(saved);
        userRole.setRole(roleUser);
        // Dates
        userRole.setCreatedAt(OffsetDateTime.now());
        userRole.setUpdatedAt(OffsetDateTime.now());

        // Sauvegarder le UserRole dans la base
        userRoleRepository.save(userRole);
        
        // Ajouter le UserRole dans la liste du user
        saved.getUserRoles().add(userRole);
        


        return UserMapper.toFullDto(saved);
    }

    // Login

    public UserFullDto login( LoginDto dto ) {
        // Cherche le user avec le email d'entrée
        User user = userRepository.findByEmail(dto.email())
           .orElseThrow(()-> new RuntimeException("Email incorrecte"));

        // Vérifie le mot de pass
        if(!passwordEncoder.matches(dto.password(),user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrecte");
        }

        return UserMapper.toFullDto(user);
    }
}
