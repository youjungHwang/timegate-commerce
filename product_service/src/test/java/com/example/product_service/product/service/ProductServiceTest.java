package com.example.product_service.product.service;

import com.example.product_service.client.stock.StockClient;
import com.example.product_service.client.stock.dto.request.StockCreateRequestDto;
import com.example.product_service.client.stock.dto.response.StockCreateResponseDto;
import com.example.product_service.client.stock.dto.response.StockResponseDto;
import com.example.product_service.common.handler.exception.CustomException;
import com.example.product_service.product.dto.request.ProductCreateRequestDto;
import com.example.product_service.product.dto.request.ProductUpdateRequestDto;
import com.example.product_service.product.dto.response.ProductCreateResponseDto;
import com.example.product_service.product.dto.response.ProductDetailsResponseDto;
import com.example.product_service.product.dto.response.ProductUpdateResponseDto;
import com.example.product_service.product.entity.Product;
import com.example.product_service.product.enums.ProductType;
import com.example.product_service.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static org.assertj.core.api.Assertions.assertThat;
import feign.FeignException;
import feign.Response;
import feign.Request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockClient stockClient;

    @Nested
    @DisplayName("상품 생성 테스트")
    class CreateProductTests {

        /**
         * TODO: 재고 생성 요청시 Feign 클라이언트 통신 실패 케이스 작성
         */
        @Test
        @DisplayName("상품 생성 요청 시 상품이 성공적으로 생성")
        void createProduct_successfully() {
            // given
            ProductCreateRequestDto requestDto = new ProductCreateRequestDto(
                    1L, "새 상품", BigDecimal.valueOf(10000), 10L,
                     ProductType.REGULAR, null, null);
            StockCreateRequestDto stockCreateRequestDto = new StockCreateRequestDto(
                    requestDto.productId(), requestDto.stock());
            StockCreateResponseDto stockCreateResponseDto = new StockCreateResponseDto(1L,10L);

            Product product = Product.builder()
                    .productName(requestDto.productName())
                    .price(requestDto.price())
                    .productType(requestDto.productType())
                    .availableFrom(requestDto.availableFrom())
                    .availableUntil(requestDto.availableUntil())
                    .build();

            Product savedProduct = Product.builder()
                    .id(1L)
                    .productName(requestDto.productName())
                    .price(requestDto.price())
                    .productType(requestDto.productType())
                    .availableFrom(requestDto.availableFrom())
                    .availableUntil(requestDto.availableUntil())
                    .build();

            // mocking
            when(stockClient.createProductStock(stockCreateRequestDto))
                    .thenReturn(stockCreateResponseDto);
            when(productRepository.save(any(Product.class)))
                    .thenReturn(savedProduct);

            // when
            ProductCreateResponseDto responseDto = productService.createProduct(requestDto);

            // then
            assertEquals(savedProduct.getId(), responseDto.productId());
            assertEquals(savedProduct.getProductName(), responseDto.productName());
            assertEquals(savedProduct.getPrice(), responseDto.price());
            assertEquals(stockCreateResponseDto.stock(), responseDto.stock());
            assertEquals(savedProduct.getProductType(), responseDto.productType());
            assertEquals(savedProduct.getAvailableFrom(), responseDto.availableFrom());
            assertEquals(savedProduct.getAvailableUntil(), responseDto.availableUntil());

            verify(stockClient).createProductStock(stockCreateRequestDto);
            verify(productRepository).save(any(Product.class));
        }

    }

    @Nested
    @DisplayName("상품 전체 조회 테스트")
    class GetAllProductsTests {

        @Test
        @DisplayName("상품 전체 조회시 상품이 페이지로 반환")
        void getAllProducts_successfully() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<Product> productList = Arrays.asList(
                    Product.builder().id(1L).productName("새 상품1").price(BigDecimal.valueOf(10000))
                            .productType(ProductType.REGULAR)
                            .availableFrom(null)
                            .availableUntil(null)
                            .build(),
                    Product.builder().id(2L).productName("새 상품2").price(BigDecimal.valueOf(15000))
                            .productType(ProductType.REGULAR)
                            .availableFrom(null)
                            .availableUntil(null)
                            .build()
            );

            Page<Product> expectedPage = new PageImpl<>(productList, pageable, productList.size());

            // mocking
            when(productRepository.findAllByProductTypeAndDeletedAtIsNull(ProductType.REGULAR, pageable))
                    .thenReturn(expectedPage);

            // when
            Page<Product> products = productService.getAllProducts(pageable);

            // then
            assertThat(products.getContent()).isEqualTo(expectedPage.getContent());
            assertThat(products.getTotalElements()).isEqualTo(expectedPage.getTotalElements());
            assertThat(products).isEqualTo(expectedPage);
        }

        @Test
        @DisplayName("상품 전체 조회 시 해당 타입의 상품이 없으면 커스텀 예외 발생")
        void getAllProducts_WhenNoProductTypeFound_ThrowsException() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(productRepository.findAllByProductTypeAndDeletedAtIsNull(ProductType.REGULAR, pageable))
                    .thenReturn(emptyPage);

            // when, then
            assertThatThrownBy(() -> productService.getAllProducts(pageable))
                    .isInstanceOf(CustomException.class);
        }

    }

    @Nested
    @DisplayName("상품 상세 조회 테스트")
    class GetProductDetailsTests {

        @Test
        @DisplayName("상품 상세 조회 성공")
        void getProductDetails_successfully() {
            // given
            Long productId = 1L;
            Product product = Product.builder()
                    .id(productId)
                    .productName("새 상품1")
                    .price(BigDecimal.valueOf(10000))
                    .productType(ProductType.REGULAR)
                    .availableFrom(null)
                    .availableUntil(null)
                    .deletedAt(null)
                    .build();

            StockResponseDto stockResponse = new StockResponseDto(productId, 10L);

            // mocking
            when(stockClient.getProductStocks(productId))
                    .thenReturn(stockResponse);
            when(productRepository.findById(any(Long.class)))
                    .thenReturn(Optional.ofNullable(product));

            // when
            ProductDetailsResponseDto result = productService.getProductDetails(productId);

            // then
            assertThat(result.productId()).isEqualTo(product.getId());
            assertThat(result.productName()).isEqualTo(product.getProductName());
            assertThat(result.price()).isEqualTo(product.getPrice());
            assertThat(result.productType()).isEqualTo(product.getProductType());
            assertThat(result.availableFrom()).isEqualTo(product.getAvailableFrom());
            assertThat(result.availableUntil()).isEqualTo(product.getAvailableUntil());
        }

        @Test
        @DisplayName("상품 상세 조회시 존재하지 않는 상품 ID를 사용시 커스텀 예외 발생")
        void getProductDetails_WhenProductNotFound_ThrowsException() {
            // given
            Long nonExistentProductId = 999L;

            // mocking
            when(productRepository.findById(nonExistentProductId))
                    .thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> productService.getProductDetails(nonExistentProductId))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("상품을 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("상품 상세 조회시 삭제된 상품을 조회시 커스텀 예외 발생")
        void getProductDetails_WhenProductIsDeleted_ThrowsException() {
            // given
            Long deletedProductId = 1L;
            Product deletedProduct = Product.builder()
                    .id(deletedProductId)
                    .productName("삭제된 상품")
                    .price(BigDecimal.valueOf(10000))
                    .productType(ProductType.REGULAR)
                    .availableFrom(null)
                    .availableUntil(null)
                    .deletedAt(LocalDateTime.now().minusDays(1))
                    .build();

            // mocking
            when(productRepository.findById(deletedProductId))
                    .thenReturn(Optional.of(deletedProduct));

            // when, then
            assertThatThrownBy(() -> productService.getProductDetails(deletedProductId))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("이미 삭제된 항목입니다.");
        }

        /**
         *  Feign 클라이언트 통신 실패 케이스
         *   TODO: 통신 실패 케이스 별로 에러 메세지 세분화
         */
        @Test
        @DisplayName("외부 서비스 타임아웃 시 커스텀 예외 발생")
        void getProductDetails_WhenTimeout_ThrowsException() {
            // given
            Long productId = 1L;

            Product product = Product.builder().id(productId).build();

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            FeignException timeoutException = FeignException.errorStatus(
                    "getProductStocks",
                    Response.builder()
                            .status(504)
                            .reason("Gateway Timeout")
                            .request(Request.create(Request.HttpMethod.GET,
                                    "http://localhost:8089/api/v1/stocks/{productId}",
                                    Collections.emptyMap(), null, null, null))
                            .build());

            when(stockClient.getProductStocks(productId)).thenThrow(timeoutException);

            // when, then
            assertThatThrownBy(() -> productService.getProductDetails(productId))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("외부 서비스 호출에 실패했습니다.");
        }

        @Test
        @DisplayName("외부 서비스에서 4XX 클라이언트 오류 반환 시 CustomException 예외 발생")
        void getProductDetails_WhenClientError_ThrowsException() {
            // given
            Long productId = 1L;
            Product product = Product.builder().id(productId).build();



            FeignException clientErrorException = FeignException.errorStatus(
                    "getProductStocks",
                    Response.builder()
                            .status(400) // HTTP 400 Bad Request
                            .reason("Bad Request")
                            .request(Request.create(Request.HttpMethod.GET,
                                    "http://localhost:8089/api/v1/stocks/{productId}",
                                    Collections.emptyMap(), null, null, null))
                            .build());

            // mocking
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(stockClient.getProductStocks(productId)).thenThrow(clientErrorException);

            // when, then
            assertThatThrownBy(() -> productService.getProductDetails(productId))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("외부 서비스 호출에 실패했습니다.");
        }

        @Test
        @DisplayName("외부 서비스에서 5XX 서버 오류 반환 시 커스텀 예외 발생")
        void getProductDetails_WhenServerError_ThrowsException() {
            // given
            Long productId = 1L;
            Product product = Product.builder().id(productId).build();

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            FeignException serverErrorException = FeignException.errorStatus(
                    "getProductStocks",
                    Response.builder()
                            .status(500) // HTTP 500 Internal Server Error
                            .reason("Internal Server Error")
                            .request(Request.create(Request.HttpMethod.GET,
                                    "http://localhost:8089/api/v1/stocks/{productId}",
                                    Collections.emptyMap(), null, null, null))
                            .build());

            when(stockClient.getProductStocks(productId)).thenThrow(serverErrorException);

            // when, then
            assertThatThrownBy(() -> productService.getProductDetails(productId))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("외부 서비스 호출에 실패했습니다.");
        }
    }

    @Nested
    @DisplayName("상품 정보 수정 테스트")
    class UpdateProductTests {

        @Test
        @DisplayName("상품 업데이트 요청 시, 상품 정보가 성공적으로 업데이트 됨")
        void updateProduct_Successfully() {
            // given
            Long productId = 1L;
            ProductUpdateRequestDto requestDto = new ProductUpdateRequestDto(
                    "변경할 상품 이름",
                    BigDecimal.valueOf(10000),
                    ProductType.RESERVED
            );

            Product product = Product.builder()
                    .id(productId)
                    .productName("새 상품")
                    .price(BigDecimal.valueOf(15000))
                    .productType(ProductType.REGULAR)
                    .availableFrom(null)
                    .availableUntil(null)
                    .deletedAt(null)
                    .build();

            // mocking
            when(productRepository.findById(productId))
                    .thenReturn(Optional.ofNullable(product));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            // when
            ProductUpdateResponseDto responseDto = productService.updateProduct(productId, requestDto);

            // then
            assertEquals(productId, responseDto.id());
            assertEquals(requestDto.productName(), responseDto.productName());
            assertEquals(requestDto.price(), responseDto.price());
            assertEquals(requestDto.productType(), responseDto.productType());
        }

        @Test
        @DisplayName("업데이트 시 상품을 찾을 수 없으면 커스텀 예외를 발생시킴")
        void updateProduct_WhenProductNotFound_ThrowsException() {
            // given
            Long nonExistingProductId = 999L;
            ProductUpdateRequestDto requestDto = new ProductUpdateRequestDto(
                    "변경할 상품 이름",
                    BigDecimal.valueOf(10000),
                    ProductType.RESERVED);

            // mocking
            when(productRepository.findById(nonExistingProductId))
                    .thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> productService.updateProduct(nonExistingProductId, requestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("상품을 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("업데이트 요청 시 상품 이름이 비어있으면 커스텀 예외를 발생시킴")
        void updateProduct_WhenNameIsEmpty_ThrowsException() {
            // given
            Long productId = 1L;
            ProductUpdateRequestDto requestDto = new ProductUpdateRequestDto(
                    "",
                    BigDecimal.valueOf(10000),
                    ProductType.RESERVED);

            // when, then
            assertThatThrownBy(() -> productService.updateProduct(productId, requestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("잘못된 요청입니다.");
        }

        @Test
        @DisplayName("업데이트 요청 시 가격이 음수이면 커스텀 예외를 발생시킴")
        void updateProduct_WhenPriceIsNegative_ThrowsException() {
            // given
            Long productId = 1L;
            ProductUpdateRequestDto requestDto = new ProductUpdateRequestDto(
                    "변경할 상품 이름",
                    BigDecimal.valueOf(-100),
                    ProductType.RESERVED);

            // when, then
            assertThatThrownBy(() -> productService.updateProduct(productId, requestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("잘못된 요청입니다.");
        }
    }

    @Nested
    @DisplayName("상품 삭제 테스트")
    class DeleteProductTests {

        @Test
        @DisplayName("상품 삭제 시, 상품이 성공적으로 소프트 삭제되어야 함")
        void deleteProduct_SuccessfullySoftDeletesProduct() {
            // given
            Long productId = 1L;
            Product product = Product.builder()
                    .id(productId)
                    .productName("새 상품")
                    .price(BigDecimal.valueOf(15000))
                    .productType(ProductType.REGULAR)
                    .availableFrom(null)
                    .availableUntil(null)
                    .deletedAt(null)
                    .build();

            // mocking
            when(productRepository.findById(productId)).thenReturn(Optional.ofNullable(product));
            doNothing().when(stockClient).deleteProductStocks(productId);

            // when
            productService.deleteProduct(productId);

            // then
            verify(productRepository).save(product);
            verify(stockClient).deleteProductStocks(productId);
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID로 조회 시 커스텀 예외 발생")
        void getProduct_WhenNotFound_ThrowsProductNotFoundException() {
            // given
            Long nonExistingProductId = 999L;

            // mocking
            when(productRepository.findById(nonExistingProductId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> productService.getProductDetails(nonExistingProductId))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("상품을 찾을 수 없습니다.");
        }


        @Test
        @DisplayName("이미 삭제된 상품을 삭제하려고 할 때, 커스텀 예외 발생")
        void deleteProduct_WhenAlreadyDeleted_ThrowsException() {
            // given
            Long productId = 1L;
            Product alreadyDeletedProduct = Product.builder()
                    .id(productId)
                    .productName("이미 삭제된 상품")
                    .price(BigDecimal.valueOf(15000))
                    .productType(ProductType.REGULAR)
                    .availableFrom(null)
                    .availableUntil(null)
                    .deletedAt(LocalDateTime.now())
                    .build();

            // mocking
            when(productRepository.findById(productId)).thenReturn(Optional.of(alreadyDeletedProduct));

            // when, then
            assertThatThrownBy(() -> productService.deleteProduct(productId))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("이미 삭제된 항목입니다.");
        }
    }
}