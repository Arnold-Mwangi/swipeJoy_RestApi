package blog.posts.p1.authentication.userManagement.enums;

import java.util.Arrays;
import java.util.List;


public enum RoleEnum {
    CLIENT(Arrays.asList(PermissionEnum.READ_ALL_PRODUCTS, PermissionEnum.BUY_PRODUCT, PermissionEnum.MAKE_PAYMENT)),


    ADMIN(Arrays.asList(PermissionEnum.READ_ALL_PRODUCTS, PermissionEnum.DELETE_ACCOUNTS, PermissionEnum.CHANGE_PERMISSIONS,PermissionEnum.CHANGE_USER_ROLE, PermissionEnum.MAKE_PAYMENT, PermissionEnum.EDIT_ACCOUNTS, PermissionEnum.DELETE_PRODUCTS, PermissionEnum.CREATE_ACCOUNTS)),

    PROVIDER(Arrays.asList(PermissionEnum.CREATE_PRODUCT, PermissionEnum.DELETE_OWN_PRODUCT, PermissionEnum.DELETE_OWN_ACCOUNT,PermissionEnum.READ_OWN_PRODUCTS, PermissionEnum.SELL_OWN_PRODUCT));

    private List<PermissionEnum> permission;

    RoleEnum(List<PermissionEnum> permission) {
        this.permission = permission;
    }

    public List<PermissionEnum> getPermission() {
        return permission;
    }

    public void setPermission(List<PermissionEnum> permission) {
        this.permission = permission;
    }
}
