src/main/java/com/example/web_bansach
├── common/                         → Phần dùng chung cho toàn hệ thống, không thuộc riêng domain nào
│   ├── config/                     → Cấu hình chung của ứng dụng
│   │   ├── CorsConfig.java         → Cấu hình CORS cho frontend gọi API
│   │   ├── SwaggerConfig.java      → Cấu hình Swagger/OpenAPI
│   │   ├── JacksonConfig.java      → Cấu hình JSON serialize/deserialize
│   │   └── WebMvcConfig.java       → Cấu hình MVC chung nếu cần
│   │
│   ├── constant/                   → Hằng số dùng chung
│   │   ├── AppConstants.java       → Các hằng số tổng quát của app
│   │   ├── RoleConstants.java      → Tên role như ADMIN, USER
│   │   ├── SecurityConstants.java  → Header, prefix token, v.v.
│   │   └── MessageConstants.java   → Message cố định dùng nhiều nơi
│   │
│   ├── exception/                  → Xử lý lỗi chung của toàn hệ thống
│   │   ├── BusinessException.java  → Lỗi nghiệp vụ
│   │   ├── ResourceNotFoundException.java → Không tìm thấy dữ liệu
│   │   ├── UnauthorizedException.java → Chưa đăng nhập / token sai
│   │   ├── ForbiddenException.java → Không đủ quyền
│   │   ├── ErrorResponse.java      → Cấu trúc response lỗi
│   │   └── GlobalExceptionHandler.java → Bắt lỗi tập trung toàn app
│   │
│   ├── response/                   → Response dùng chung
│   │   ├── ApiResponse.java        → Wrapper response chung
│   │   ├── PageResponse.java       → Response phân trang
│   │   └── PaginationMeta.java     → Metadata phân trang
│   │
│   ├── util/                       → Hàm tiện ích dùng chung
│   │   ├── DateUtils.java          → Xử lý ngày giờ
│   │   ├── SlugUtils.java          → Sinh slug
│   │   ├── CurrencyUtils.java      → Hỗ trợ format tiền
│   │   └── FileUtils.java          → Hỗ trợ file nếu cần
│   │
│   └── mapper/                     → Mapper dùng chung hoặc base mapper
│       └── BaseMapper.java         → Interface/base mapper nếu bạn muốn chuẩn hóa
│
├── security/                       → Toàn bộ phần bảo mật và xác thực
│   ├── config/                     → Cấu hình Spring Security
│   │   └── SecurityConfiguration.java → Khai báo filter chain, route public/private
│   │
│   ├── jwt/                        → Xử lý JWT
│   │   ├── JwtAuthenticationFilter.java → Filter đọc và kiểm tra token
│   │   ├── JwtTokenProvider.java   → Tạo, parse, validate JWT
│   │   └── JwtProperties.java      → Cấu hình JWT secret, expiration
│   │
│   ├── principal/                  → Đại diện user đang đăng nhập
│   │   └── UserPrincipal.java      → Chứa id, email, role của user hiện tại
│   │
│   ├── service/                    → Service phục vụ security
│   │   └── CustomUserDetailsService.java → Load user từ DB cho Spring Security
│   │
│   └── handler/                    → Xử lý lỗi liên quan security
│       ├── JwtAuthenticationEntryPoint.java → Lỗi chưa xác thực
│       └── JwtAccessDeniedHandler.java → Lỗi không đủ quyền
│
├── infrastructure/                 → Tích hợp hạ tầng và dịch vụ ngoài
│   ├── cloudinary/                 → Tích hợp Cloudinary
│   │   ├── CloudinaryConfig.java   → Cấu hình Cloudinary
│   │   └── CloudinaryFileStorageService.java → Upload/xóa ảnh trên Cloudinary
│   │
│   ├── payment/                    → Tích hợp cổng thanh toán
│   │   ├── PaymentGateway.java     → Interface abstraction cho cổng thanh toán
│   │   ├── VNPayGateway.java       → Tích hợp VNPay
│   │   ├── MomoGateway.java        → Tích hợp Momo nếu có
│   │   └── PaymentStrategyFactory.java → Chọn provider thanh toán phù hợp
│   │
│   ├── messaging/                  → Tích hợp queue/message broker nếu có
│   │   └── NotificationProducer.java → Gửi message ra queue
│   │
│   ├── persistence/                → Cấu hình và hỗ trợ persistence
│   │   ├── JpaConfig.java          → Config JPA nếu cần
│   │   ├── AuditingConfig.java     → Tự động createdAt/updatedAt
│   │   └── BaseEntity.java         → Entity base dùng chung
│   │
│   └── external/                   → Tích hợp dịch vụ ngoài khác
│       └── EmailSender.java        → Gửi email qua provider ngoài
│
└── module/                         → Các domain/chức năng chính của hệ thống
    ├── auth/                       → Nghiệp vụ xác thực: login, register
    │   ├── controller/
    │   │   └── AuthController.java → API login/register
    │   ├── service/
    │   │   └── AuthService.java    → Xử lý nghiệp vụ auth
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── LoginRequest.java → Dữ liệu đăng nhập
    │   │   │   └── RegisterRequest.java → Dữ liệu đăng ký
    │   │   └── response/
    │   │       └── AuthResponse.java → Token + thông tin user sau login
    │   └── mapper/
    │       └── AuthMapper.java     → Map dữ liệu auth nếu cần
    │
    ├── user/                       → Nghiệp vụ người dùng
    │   ├── controller/
    │   │   ├── UserController.java → API user tự xem/sửa profile
    │   │   └── UserAdminController.java → API admin quản lý user
    │   ├── service/
    │   │   ├── UserService.java    → Service chính của user
    │   │   ├── UserQueryService.java → Lấy danh sách/chi tiết user
    │   │   └── UserCommandService.java → Tạo/sửa/khóa user
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── UpdateProfileRequest.java → Sửa hồ sơ
    │   │   │   └── ChangePasswordRequest.java → Đổi mật khẩu
    │   │   └── response/
    │   │       ├── UserResponse.java → Thông tin user cơ bản
    │   │       └── UserDetailResponse.java → Chi tiết user
    │   ├── entity/
    │   │   └── User.java           → Entity người dùng
    │   ├── repository/
    │   │   └── UserRepository.java → Truy cập DB của user
    │   ├── mapper/
    │   │   └── UserMapper.java     → Map entity ↔ DTO
    │   └── validator/
    │       └── UserValidator.java  → Validate rule nghiệp vụ của user
    │
    ├── author/                     → Nghiệp vụ tác giả
    │   ├── controller/
    │   │   └── AuthorController.java → API quản lý / lấy tác giả
    │   ├── service/
    │   │   └── AuthorService.java  → Xử lý nghiệp vụ author
    │   ├── dto/
    │   ├── entity/
    │   │   └── Author.java         → Entity tác giả
    │   ├── repository/
    │   │   └── AuthorRepository.java → Truy cập DB author
    │   └── mapper/
    │       └── AuthorMapper.java   → Map dữ liệu author
    │
    ├── category/                   → Nghiệp vụ danh mục sách
    │   ├── controller/
    │   │   ├── CategoryController.java → API danh mục cho user
    │   │   └── CategoryAdminController.java → API admin quản lý danh mục
    │   ├── service/
    │   │   └── CategoryService.java → Xử lý nghiệp vụ category
    │   ├── dto/
    │   ├── entity/
    │   │   └── Category.java       → Entity danh mục
    │   ├── repository/
    │   │   └── CategoryRepository.java → Truy cập DB category
    │   └── mapper/
    │       └── CategoryMapper.java → Map dữ liệu category
    │
    ├── book/                       → Nghiệp vụ sách
    │   ├── controller/
    │   │   ├── BookPublicController.java → API danh sách, chi tiết sách cho user
    │   │   └── BookAdminController.java → API admin thêm/sửa/xóa sách
    │   ├── service/
    │   │   ├── BookService.java    → Service tổng nếu bạn muốn giữ một cổng chính
    │   │   ├── BookQueryService.java → Lấy danh sách, chi tiết, search, filter
    │   │   ├── BookCommandService.java → Tạo, sửa, xóa, đổi trạng thái sách
    │   │   └── BookValidationService.java → Rule validate nghiệp vụ của sách
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── CreateBookRequest.java → Dữ liệu tạo sách
    │   │   │   ├── UpdateBookRequest.java → Dữ liệu sửa sách
    │   │   │   └── BookSearchRequest.java → Điều kiện tìm kiếm/lọc
    │   │   └── response/
    │   │       ├── BookResponse.java → Dữ liệu sách cơ bản
    │   │       ├── BookDetailResponse.java → Chi tiết sách
    │   │       └── BookAdminResponse.java → Response phục vụ màn admin
    │   ├── entity/
    │   │   └── Book.java           → Entity sách
    │   ├── repository/
    │   │   └── BookRepository.java → Truy cập DB của sách
    │   ├── mapper/
    │   │   └── BookMapper.java     → Map entity sách sang DTO
    │   ├── validator/
    │   │   └── BookValidator.java  → Validate rule như tên, giá, category
    │   └── specification/
    │       └── BookSpecification.java → Query động cho search/filter
    │
    ├── inventory/                  → Nghiệp vụ tồn kho
    │   ├── controller/
    │   │   └── InventoryAdminController.java → API admin chỉnh tồn kho
    │   ├── service/
    │   │   ├── InventoryService.java → Xử lý tồn kho
    │   │   └── InventoryCheckService.java → Kiểm tra còn hàng/đủ hàng
    │   ├── dto/
    │   │   ├── request/
    │   │   │   └── UpdateInventoryRequest.java → Dữ liệu cập nhật tồn kho
    │   │   └── response/
    │   │       └── InventoryResponse.java → Thông tin tồn kho
    │   ├── entity/
    │   │   └── Inventory.java      → Entity tồn kho
    │   ├── repository/
    │   │   └── InventoryRepository.java → Truy cập DB inventory
    │   └── validator/
    │       └── InventoryValidator.java → Validate rule tồn kho
    │
    ├── cart/                       → Nghiệp vụ giỏ hàng
    │   ├── controller/
    │   │   └── CartController.java → API thêm/xóa/sửa giỏ hàng
    │   ├── service/
    │   │   ├── CartService.java    → Service chính của giỏ hàng
    │   │   └── CartPricingService.java → Tính tổng tạm thời của giỏ hàng
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── AddToCartRequest.java → Thêm sách vào giỏ
    │   │   │   └── UpdateCartItemRequest.java → Sửa số lượng item
    │   │   └── response/
    │   │       ├── CartResponse.java → Toàn bộ giỏ hàng
    │   │       └── CartItemResponse.java → Từng item trong giỏ
    │   ├── entity/
    │   │   ├── Cart.java           → Entity giỏ hàng
    │   │   └── CartItem.java       → Entity item trong giỏ
    │   ├── repository/
    │   │   ├── CartRepository.java → DB cart
    │   │   └── CartItemRepository.java → DB cart item
    │   ├── mapper/
    │   │   └── CartMapper.java     → Map dữ liệu cart
    │   └── validator/
    │       └── CartValidator.java  → Validate rule giỏ hàng
    │
    ├── order/                      → Nghiệp vụ đơn hàng
    │   ├── controller/
    │   │   ├── OrderController.java → API user tạo và xem đơn
    │   │   └── OrderAdminController.java → API admin xử lý đơn hàng
    │   ├── service/
    │   │   ├── OrderService.java   → Service tổng nếu cần
    │   │   ├── OrderCommandService.java → Tạo đơn, hủy đơn, cập nhật trạng thái
    │   │   ├── OrderQueryService.java → Lấy danh sách, chi tiết, lịch sử đơn
    │   │   ├── OrderStatusService.java → Rule chuyển trạng thái đơn
    │   │   └── CheckoutFacade.java → Gom luồng checkout nếu bạn muốn dùng Facade
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── CreateOrderRequest.java → Dữ liệu tạo đơn
    │   │   │   ├── CancelOrderRequest.java → Dữ liệu hủy đơn
    │   │   │   └── UpdateOrderStatusRequest.java → Đổi trạng thái đơn
    │   │   └── response/
    │   │       ├── OrderResponse.java → Đơn hàng cơ bản
    │   │       ├── OrderDetailResponse.java → Chi tiết đơn hàng
    │   │       └── OrderAdminResponse.java → Response cho admin
    │   ├── entity/
    │   │   ├── Order.java          → Entity đơn hàng
    │   │   └── OrderItem.java      → Entity item trong đơn
    │   ├── repository/
    │   │   ├── OrderRepository.java → DB đơn hàng
    │   │   └── OrderItemRepository.java → DB item đơn hàng
    │   ├── mapper/
    │   │   └── OrderMapper.java    → Map entity đơn hàng sang DTO
    │   ├── validator/
    │   │   └── OrderValidator.java → Validate rule nghiệp vụ order
    │   └── specification/
    │       └── OrderSpecification.java → Filter/search order cho admin
    │
    ├── payment/                    → Nghiệp vụ thanh toán trong hệ thống
    │   ├── controller/
    │   │   └── PaymentController.java → API tạo payment, callback, tra cứu payment
    │   ├── service/
    │   │   ├── PaymentService.java → Xử lý payment trong app
    │   │   ├── PaymentCallbackService.java → Xử lý callback từ cổng thanh toán
    │   │   └── PaymentStatusService.java → Quản lý trạng thái payment
    │   ├── dto/
    │   │   ├── request/
    │   │   │   └── CreatePaymentRequest.java → Tạo payment
    │   │   └── response/
    │   │       └── PaymentResponse.java → Response payment
    │   ├── entity/
    │   │   └── Payment.java        → Entity thanh toán
    │   ├── repository/
    │   │   └── PaymentRepository.java → DB payment
    │   ├── mapper/
    │   │   └── PaymentMapper.java  → Map payment
    │   └── validator/
    │       └── PaymentValidator.java → Validate rule payment
    │
    ├── review/                     → Nghiệp vụ đánh giá sách
    │   ├── controller/
    │   │   └── ReviewController.java → API tạo/sửa/xóa/xem review
    │   ├── service/
    │   │   └── ReviewService.java  → Xử lý review
    │   ├── dto/
    │   │   ├── request/
    │   │   │   └── CreateReviewRequest.java → Tạo review
    │   │   └── response/
    │   │       └── ReviewResponse.java → Response review
    │   ├── entity/
    │   │   └── Review.java         → Entity review
    │   ├── repository/
    │   │   └── ReviewRepository.java → DB review
    │   ├── mapper/
    │   │   └── ReviewMapper.java   → Map review
    │   └── validator/
    │       └── ReviewValidator.java → Validate rule đánh giá
    │
    ├── voucher/                    → Nghiệp vụ mã giảm giá
    │   ├── controller/
    │   │   ├── VoucherController.java → API áp voucher cho user
    │   │   └── VoucherAdminController.java → API admin tạo/sửa/xóa voucher
    │   ├── service/
    │   │   ├── VoucherService.java → Xử lý voucher
    │   │   └── VoucherApplyService.java → Kiểm tra và áp voucher
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── CreateVoucherRequest.java → Tạo voucher
    │   │   │   └── ApplyVoucherRequest.java → Áp voucher vào đơn/giỏ
    │   │   └── response/
    │   │       └── VoucherResponse.java → Response voucher
    │   ├── entity/
    │   │   └── Voucher.java        → Entity voucher
    │   ├── repository/
    │   │   └── VoucherRepository.java → DB voucher
    │   ├── mapper/
    │   │   └── VoucherMapper.java  → Map voucher
    │   └── validator/
    │       └── VoucherValidator.java → Validate rule voucher
    │
    └── wishlist/                   → Nghiệp vụ danh sách yêu thích
        ├── controller/
        │   └── WishlistController.java → API thêm/xóa/xem wishlist
        ├── service/
        │   └── WishlistService.java → Xử lý wishlist
        ├── dto/
        │   ├── request/
        │   │   └── AddWishlistRequest.java → Thêm item yêu thích
        │   └── response/
        │       └── WishlistResponse.java → Response wishlist
        ├── entity/
        │   └── Wishlist.java       → Entity wishlist
        ├── repository/
        │   └── WishlistRepository.java → DB wishlist
        └── mapper/
            └── WishlistMapper.java → Map wishlist