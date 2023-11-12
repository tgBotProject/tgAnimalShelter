package pro.sky.teamproject.tgBot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pro.sky.teamproject.tgBot.model.user.Role;
import pro.sky.teamproject.tgBot.model.user.User;
import pro.sky.teamproject.tgBot.repository.UserRepository;
import pro.sky.teamproject.tgBot.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @SpyBean
    private UserServiceImpl userService;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void findAllUser() throws Exception {
        User user1 = new User();
        user1.setId(1l);
        user1.setChatId(1L);
        user1.setName("name");
        user1.setPhone("phone");

        User user2 = new User();
        user2.setId(1l);
        user2.setChatId(1L);
        user2.setName("name");
        user2.setPhone("phone");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].chatId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].phone").value("phone"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].chatId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].phone").value("phone"));
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void findUserTest() throws Exception {
        User user1 = new User();
        user1.setId(1l);
        user1.setChatId(1L);
        user1.setName("name");
        user1.setPhone("phone");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.chatId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("phone"));

        verify(userRepository, times(1)).findById(1L);
        verify(userService, times(1)).findUser(1L);
    }

    @Test
    public void findUserByRoleTest() throws Exception {
        User user1 = new User();
        user1.setName("name");
        user1.setRole(Role.VOLUNTEER);

        User user2 = new User();
        user2.setName("user2");
        user2.setRole(Role.ADOPTER);

        List<User> expected = new ArrayList<>();
        expected.add(user1);
        expected.add(user2);


        when(userRepository.findUsersByRole(Role.ADOPTER)).thenReturn(expected);

        List<User> users = userService.findUsersByRole(String.valueOf(Role.ADOPTER));

        Assert.assertEquals(expected, users);
    }

    @Test
    public void updateUser() throws Exception {
        long id = 1L;
        long chatId = 1L;

        User user = new User(id, chatId,"name","phone", null, null,null);
        User updatedUser = new User(id, chatId,"new name", "new phone", null,null,null);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/user", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedUser.getName()))
                .andExpect(jsonPath("$.phone").value(updatedUser.getPhone()))
                .andDo(print());
    }

    @Test
    void testDeleteUser() throws Exception {

        Long id = 1L;
        User user = new User();
        user.setId(id);

        given(userRepository.findById(id)).willReturn(Optional.of(user));
        doNothing().when(userService).deleteUser(id);
        mockMvc.perform(MockMvcRequestBuilders.delete("/user/{id}", id))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(anyLong());
    }
}

