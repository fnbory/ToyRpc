package config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: fnbory
 * @Date: 2019/9/30 14:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceConfig {
    private String address;
}
