package com.system.watchCar.dto;

import javax.validation.groups.Default;

public class UserTypeGroups {
    public interface Policial extends Default {}
    public interface Gestor extends Default {}
    public interface Cidadao extends Default {}
}
