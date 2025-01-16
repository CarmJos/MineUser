package cc.carm.lib.minecraft.user.data;

public enum UserKeyType {
    ID("id"), UUID("uuid"), NAME("name");

    final String dataKey;

    UserKeyType(String dataKey) {
        this.dataKey = dataKey;
    }

    public String dataKey() {
        return dataKey;
    }
}