# Java 代码规范

本文档是 MES 学习项目的 Java 代码强制规范。所有 `backend/src/main/java` 下新增、修改和复制的 Java 代码都必须遵守本规范。

## 1. 强制要求

1. 所有公开类、接口、枚举、注解、构造函数和方法都必须有 Javadoc。
2. 所有方法和接口方法的 Javadoc 必须说明：用途、每个参数、返回值；异常行为也必须说明。
3. 私有方法原则上也必须有 Javadoc；当私有方法逻辑超过 5 行或包含业务判断时，必须有 Javadoc。
4. 大块代码必须通过行注释解释业务目的、关键判断、事务边界、外部调用和异常处理原因。
5. 配置代码必须通过行注释说明配置项的用途、默认值、依赖服务和修改影响。
6. 禁止使用无意义注释，例如“执行方法”“设置变量”“调用接口”等不能解释原因的注释。
7. 注释必须描述当前代码的真实行为，不得保留过期说明、复制粘贴的错误参数名或错误返回值。
8. 禁止使用 Java `var` 局部变量声明；所有变量必须显式声明具体、准确的类型。
9. Java 代码统一使用大括号尾行风格：左大括号与声明或控制语句位于同一行末尾，右大括号独占一行。

## 2. 编码风格

### 2.1 大括号尾行风格

类、方法、条件、循环、异常处理和 Lambda 代码都必须使用大括号尾行风格（K&R 风格）：

```java
public class DeviceService {
    public DeviceResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("设备 ID 不能为空");
        }
        return repository.findById(id);
    }
}
```

禁止将左大括号单独放在下一行：

```java
// 错误：左大括号不能独占一行。
public class DeviceService
{
}
```

右大括号应与对应代码块的开始位置对齐；`else`、`catch` 和 `finally` 应与前一个代码块的右大括号处于同一行：

```java
try {
    service.execute();
} catch (RuntimeException exception) {
    log.error("业务执行失败", exception);
} finally {
    resource.close();
}
```

## 3. Javadoc 规范

### 2.1 方法和接口方法

每个方法必须按照以下顺序书写：

1. 一句话说明方法作用。
2. `@param`：按照参数列表顺序说明每个参数。
3. `@return`：说明返回对象、状态或数据含义；无返回值的方法写明“无返回值”。
4. `@throws`：说明方法主动抛出的、或调用者必须处理的异常。

```java
/**
 * 根据设备编号查询设备详情，并返回当前设备状态。
 *
 * @param code 设备唯一编码，不允许为空
 * @return 设备详情；设备不存在时抛出 ResourceNotFoundException
 * @throws ResourceNotFoundException 设备编码不存在
 */
public DeviceResponse findByCode(String code) {
    return repository.findByCode(code)
            .map(DeviceResponse::from)
            .orElseThrow(() -> new ResourceNotFoundException("设备不存在: " + code));
}
```

无返回值的方法也必须描述结果：

```java
/**
 * 修改设备运行状态，并记录状态变更日志。
 *
 * @param deviceId 设备主键
 * @param status 目标状态
 * @return 无返回值
 * @throws ResourceNotFoundException 设备不存在
 * @throws IllegalArgumentException 状态值不受支持
 */
public void changeStatus(Long deviceId, String status) {
    // 先校验目标状态，避免非法状态写入数据库。
    DeviceStatus targetStatus = DeviceStatus.valueOf(status);
    // 设备更新和日志记录必须在同一业务操作中完成。
    repository.updateStatus(deviceId, targetStatus);
}
```

### 2.2 接口、类和构造函数

接口必须说明职责；接口方法仍然需要独立写 Javadoc，不能只依赖接口整体注释。

```java
/**
 * 提供设备持久化访问能力，屏蔽应用层与数据库实现的耦合。
 */
public interface DeviceRepository {

    /**
     * 根据设备编码查询设备。
     *
     * @param code 设备唯一编码
     * @return 设备实体；不存在时返回空 Optional
     */
    Optional<Device> findByCode(String code);
}
```

构造函数必须说明依赖参数：

```java
/**
 * 创建设备应用服务。
 *
 * @param repository 设备持久化仓库
 * @param auditLogService 审计日志服务
 */
public DeviceService(DeviceRepository repository, AuditLogService auditLogService) {
    this.repository = repository;
    this.auditLogService = auditLogService;
}
```

### 2.3 Controller 方法

Controller 的 Javadoc 必须说明 HTTP 用途、认证要求、请求参数和响应结果。对于 DTO 参数，要说明字段校验和业务限制。

```java
/**
 * 提交设备状态变更申请。
 *
 * @param request 设备状态变更请求，包含设备 ID 和目标状态
 * @param authentication 当前登录用户，用于记录申请人
 * @return 创建后的审批申请
 * @throws AccessDeniedException 当前用户没有设备变更权限
 */
@PostMapping("/status-change")
public ApiResponse<ApprovalRequest> submitStatusChange(
        @Valid @RequestBody StatusChangeRequest request,
        Authentication authentication) {
    return ApiResponse.ok(service.submit(request, authentication.getName()));
}
```

## 4. 大块代码注释规范

当连续代码超过约 8 行，或代码包含多个业务步骤时，必须按“目的”添加行注释。注释应放在对应代码之前，不要在每一行重复描述语法。

```java
// 申请通过后才执行设备变更；审批拒绝时必须保持设备原状态。
if (approved) {
    applyApprovedChange(request);
}

// 更新审批状态，再发布事件，使管理员和申请人都能看到最终结果。
request.review(reviewer, targetStatus, remark);
eventPublisher.publish(ApprovalEvent.from(request));
```

涉及事务、消息、缓存或远程调用时，必须说明顺序和失败影响：

```java
// 先写入数据库形成可靠事实，再发布 RabbitMQ 事件；消息发布失败时由消费者机制重试。
ApprovalRequest saved = repository.save(request);
eventPublisher.publish(ApprovalEvent.from(saved));

// 删除缓存，避免下一次查询继续返回旧设备状态。
cacheService.evictDevice(saved.getDeviceId());
```

## 5. 配置代码和配置文件注释规范

Java 配置类、YAML、JSON、XML 和 Docker 配置中的关键配置必须有行注释。注释至少说明用途、默认值或依赖服务。

```java
// 使用 host.docker.internal 访问宿主机上的 PostgreSQL，Docker 环境不要改成 localhost。
String databaseHost = environment.getProperty("DB_HOST", "host.docker.internal");

// Redis 用于缓存和 Centrifugo 的跨实例消息分发，必须与部署文件中的服务名一致。
String redisHost = environment.getProperty("REDIS_HOST", "localhost");
```

YAML 示例：

```yaml
spring:
  datasource:
    # 本机 PostgreSQL 的数据库名；修改数据库时必须同步检查建表和权限配置。
    url: jdbc:postgresql://localhost:5432/mes
    # 密码通过环境变量覆盖，禁止把生产密码提交到代码仓库。
    password: ${DB_PASSWORD:mes}
```

配置注释禁止写入密码、令牌、API Key 等真实敏感信息。

## 6. 注释内容要求

- 解释“为什么这样做”，而不是复述“代码做了什么”。
- 参数注释必须使用真实参数名，不能使用“参数一”“数据”等模糊描述。
- 返回值必须说明数据结构、状态含义、空值行为或异常行为。
- 异步方法必须说明执行时机、线程/消息队列边界和失败重试策略。
- 数据库方法必须说明查询条件、排序规则、分页规则和事务要求。
- 公共接口变更时，必须同步更新 Javadoc 和调用方相关说明。
- 不确定的业务规则不得通过注释猜测；应先确认规则或标注待确认事项。

## 7. 禁止事项

- 禁止无 Javadoc 的 public/protected 方法或接口方法。
- 禁止使用 `var` 声明变量，必须使用明确的变量类型，禁止用宽泛类型掩盖真实数据类型。
- 禁止使用左大括号单独换行，禁止混用 Allman、GNU 等其他大括号风格。
- 禁止只写 `TODO` 而不说明待办原因、影响范围和完成条件。
- 禁止使用错误、乱码或与实际行为不符的注释。
- 禁止用注释掩盖复杂逻辑；复杂逻辑应拆分为命名清晰的方法。
- 禁止将敏感配置写入注释、示例代码、日志或异常信息。

## 8. 提交前检查清单

- [ ] 新增或修改的类、接口、构造函数和方法都有 Javadoc。
- [ ] 每个方法都有完整的 `@param`、`@return` 和必要的 `@throws`。
- [ ] Controller、Service、Repository 和消息消费者的职责已说明。
- [ ] 事务、数据库、缓存、MQ、WebSocket 和远程调用代码有关键行注释。
- [ ] YAML、JSON、Java 配置中的关键配置项有行注释。
- [ ] 注释与代码行为一致，没有过期说明和敏感信息。
- [ ] Java 代码未使用 `var`，所有变量均使用显式、准确的类型声明。
- [ ] 左大括号位于声明或控制语句行尾，右大括号独占并正确对齐。
- [ ] 已运行 Maven 编译或测试，并在修改后重新检查 Javadoc。
