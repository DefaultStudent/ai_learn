package com.example.mes.domain.device;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mes_device")
public class Device {
    /** 设备主数据实体；Hibernate 启动时会自动创建 mes_device 表。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 设备编码，用于设备身份识别。 */
    @Column(nullable = false, unique = true, length = 64)
    private String code;

    /** 设备显示名称。 */
    @Column(nullable = false, length = 128)
    private String name;

    /** 设备当前状态，例如 ONLINE、IDLE、OFFLINE。 */
    @Column(nullable = false, length = 32)
    private String status = "OFFLINE";

    protected Device() {}

    public Device(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getStatus() { return status; }
}
