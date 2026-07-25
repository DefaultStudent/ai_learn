package com.example.mes.device.infrastructure;

import com.example.mes.domain.device.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/** 设备持久化端口：Spring Data 根据方法和 JPQL 自动实现。 */
public interface DeviceRepository extends JpaRepository<Device, Long> {
  /**
   * 判断设备编码是否已存在。
   * @param code 待检查的设备编码
   * @return 编码存在时返回 true
   */
  boolean existsByCode(String code);

  /**
   * 判断除指定设备外是否存在相同编码。
   * @param code 待检查的设备编码
   * @param id 当前设备主键
   * @return 其他设备占用编码时返回 true
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /** 主页只展示最近创建的设备，避免一次加载全部设备主数据。 */
  /** @return 最近创建的最多 5 台设备 */
  List<Device> findTop5ByOrderByIdDesc();

  /** 统计全部设备中的在线数量，不受主页最近 5 台展示限制。 */
  /**
   * @param status 要统计的设备状态
   * @return 符合状态的设备数量
   */
  long countByStatus(String status);

  @Query("""
      select d from Device d
      where (:keyword = '' or lower(d.code) like lower(concat('%', :keyword, '%'))
          or lower(d.name) like lower(concat('%', :keyword, '%')))
        and (:status is null or d.status = :status)
      """)
  /**
   * 按关键词和状态分页搜索设备。
   * @param keyword 设备编码或名称关键词
   * @param status 设备状态，可为空
   * @param pageable 分页和排序参数
   * @return 设备分页结果
   */
  Page<Device> search(@Param("keyword") String keyword, @Param("status") String status, Pageable pageable);
}
