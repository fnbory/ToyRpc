package Server;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: fnbory
 * @Date: 2019/8/17 18:44
 */

@Getter
@Setter
@NoArgsConstructor
public class Provider implements  Serializable,Cloneable {

    private String ServiceName;

    private Integer port;

    private String host;

    private String version;

    private Integer weight;

    private String serialization;

    @Override
    public Provider clone() throws CloneNotSupportedException {
        return (Provider)super.clone();
    }

    public String buildInfo(){
        return getHost()+":"+getPort()+":"+getServiceName()+":"+getVersion();
    }

    @Override
    public int hashCode() {
        int result = getServiceName().hashCode();
        result = 31 * result + getHost().hashCode();
        result = 31 * result + getPort().hashCode();
        result = 31 * result + getVersion().hashCode();
        result = 31 * result + getWeight().hashCode();
        result = 31 * result + (getSerialization() != null ? getSerialization().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Provider)) return false;

        Provider provider = (Provider) o;

        if (!getServiceName().equals(provider.getServiceName())) return false;
        if (!getHost().equals(provider.getHost())) return false;
        if (!getPort().equals(provider.getPort())) return false;
        if (!getVersion().equals(provider.getVersion())) return false;
        if (!getWeight().equals(provider.getWeight())) return false;
        return getSerialization() != null ? getSerialization().equals(provider.getSerialization()) : provider.getSerialization() == null;
    }
}
