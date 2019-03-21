package org.wing.mocker.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.wing.mocker.core.MockData;
import org.wing.mocker.http.mode.Dog;

import java.util.List;

public class CodeDemoTest {

    @Test
    public void testGetType() throws JsonProcessingException {
        class A extends ParameterizedTypeReference<List<Dog>>{

        }
        System.out.println(new ObjectMapper().writeValueAsString(new MockData().mock(new A().getType())));
    }
}
