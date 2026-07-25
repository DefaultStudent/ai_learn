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

    /** JPA 使用的无参构造器。 */
    protected Device() {}

    /**
     * 创建设备实体。
     * @param code 设备唯一编码
     * @param name 设备显示名称
     */
    public Device(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 修改可编辑的设备主数据。
     * @param code 新的设备编码
     * @param name 新的设备名称
     */
    public void update(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 由管理接口或遥测接入更新设备状态。
     * @param status 新的设备状态
     */
    public void changeStatus(String status) { this.status = status; }

    /** @return 设备主键 */
    public Long getId() { return id; }
    /** @return 设备编码 */
    public String getCode() { return code; }
    /** @return 设备名称 */
    public String getName() { return name; }
    /** @return 设备当前状态 */
    public String getStatus() { return status; }
}
