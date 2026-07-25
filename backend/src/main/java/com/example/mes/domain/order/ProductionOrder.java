package com.example.mes.domain.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mes_production_order")
public class ProductionOrder {
    /** 生产工单实体；Hibernate 启动时会自动创建 mes_production_order 表。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 对外展示的工单号。 */
    @Column(nullable = false, unique = true, length = 64)
    private String orderNo;

    /** 产品名称。 */
    @Column(nullable = false, length = 128)
    private String productName;

    /** 计划生产数量。 */
    @Column(nullable = false)
    private Integer plannedQuantity;

    /** 工单生命周期状态。 */
    @Column(nullable = false, length = 32)
    private String status = "CREATED";

    /** JPA 使用的无参构造器。 */
    protected ProductionOrder() {}

    /**
     * 创建生产工单实体。
     * @param orderNo 对外工单号
     * @param productName 产品名称
     * @param plannedQuantity 计划生产数量
     */
    public ProductionOrder(String orderNo, String productName, Integer plannedQuantity) {
        this.orderNo = orderNo;
        this.productName = productName;
        this.plannedQuantity = plannedQuantity;
    }

    /** @return 工单主键 */
    public Long getId() { return id; }
    /** @return 工单号 */
    public String getOrderNo() { return orderNo; }
    /** @return 产品名称 */
    public String getProductName() { return productName; }
    /** @return 计划生产数量 */
    public Integer getPlannedQuantity() { return plannedQuantity; }
    /** @return 工单状态 */
    public String getStatus() { return status; }
}
