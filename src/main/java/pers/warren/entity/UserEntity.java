package pers.warren.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserEntity extends CommonEntity{

    private String name;

    private String sex;

    public String userId;
}
