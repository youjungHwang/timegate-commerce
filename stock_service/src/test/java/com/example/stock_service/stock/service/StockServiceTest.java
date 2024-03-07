package com.example.stock_service.stock.service;

import com.example.stock_service.client.dto.request.StockRequestDto;
import com.example.stock_service.client.dto.response.StockCreateResponseDto;
import com.example.stock_service.client.dto.response.StockResponseDto;
import com.example.stock_service.common.handler.exception.CustomException;
import com.example.stock_service.stock.entity.Stock;
import com.example.stock_service.stock.repository.StockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {
    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @Nested
    @DisplayName("상품 재고 조회 테스트")
    class GetProductStockTests {

        @Test
        @DisplayName("재고 조회 시 재고가 없을 경우 커스텀 예외 발생")
        void checkStock_AndThrowException_IfNotAvailable() {
            // given
            Long productId = 1L;
            Stock stock = new Stock(productId, 10L);

            // mocking
            when(stockRepository.findById(any(Long.class)))
                    .thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(()-> stockService.getProductStock(productId))
                    .isInstanceOf(CustomException.class);

        }

        @Test
        @DisplayName("재고 조회 시 재고 반환 성공")
        void returnStock_WhenQueried() {
            // given
            Long productId = 1L;
            Stock stock = new Stock(productId, 10L);

            // mocking
            when(stockRepository.findById(any(Long.class)))
                    .thenReturn(Optional.ofNullable(stock));

            // when
            Long result = stockService.getProductStock(productId);

            // then
            assertThat(result).isEqualTo(10L);
        }
    }

    @Nested
    @DisplayName("상품 재고 감소 테스트")
    class DecreaseStockTests {

        @Test
        @DisplayName("재고 감소 성공")
        void decreaseStock_successfully() {
            // given
            Long productId = 1L;
            Long quantity = 100L;
            Long currentStock = 150L;
            Stock stock = new Stock(productId, currentStock);

            // mocking
            when(stockRepository.findById(any(Long.class)))
                    .thenReturn(Optional.ofNullable(stock));

            // when
            stockService.decreaseStock(productId, quantity);

            // then
            assertThat(stock.getStock()).isEqualTo(50L);
            verify(stockRepository).save(stock);
        }

        @Test
        @DisplayName("재고 감소 시 요청된 수량이 현재 재고보다 많을 경우 예외 발생")
        void WhenRequesteQuantity_Is_Greater_Than_CurrentStock_AndThrowException() {
            // given
            Long productId = 1L;
            Long quantity = 200L;
            Long currentStock = 150L;
            Stock stock = new Stock(productId, currentStock);

            // mocking
            when(stockRepository.findById(any(Long.class)))
                    .thenReturn(Optional.ofNullable(stock));

            // when, then
            assertThatThrownBy(()-> stockService.decreaseStock(productId, quantity))
                    .isInstanceOf(CustomException.class);
        }
    }

    @Nested
    @DisplayName("상품 재고 생성 테스트")
    class CreateProductStockTests {

        @Test
        @DisplayName("상품 재고 생성 성공")
        void createProductStock_successfully() {
            // given
            Long productId = 1L;
            Long quantity = 100L;
            Stock stock = new Stock(productId, quantity);

            // mocking
            when(stockRepository.existsByProductId(any(Long.class)))
                    .thenReturn(false);
            when(stockRepository.save(any(Stock.class)))
                    .thenReturn(stock);

            // when
            StockCreateResponseDto result = stockService.createProductStock(productId, quantity);

            // then
            verify(stockRepository).save(any(Stock.class));
            assertThat(result.productId()).isEqualTo(productId);
            assertThat(result.stock()).isEqualTo(quantity);
        }

        @Test
        @DisplayName("상품 재고 생성 시 이미 재고가 있을 경우 예외 처리")
        void createProductStock_whenStockAlreadyExists_thenThrowException() {
            // given
            Long productId = 1L;
            Long quantity = 100L;

            // mocking
            when(stockRepository.existsByProductId(any(Long.class)))
                    .thenReturn(true);

            // when, then
            assertThatThrownBy(()-> stockService.createProductStock(productId, quantity))
                    .isInstanceOf(CustomException.class);
        }
    }

    @Nested
    @DisplayName("상품 재고 조회 요청 테스트")
    class GetProductStocksTests{

        @Test
        @DisplayName("상품의 재고 조회 성공")
        void getProductStocks_successfully() {
            // given
            Long productId = 1L;
            Long expectedStock = 100L;
            Stock stock = new Stock(productId, expectedStock);

            // mocking
            when(stockRepository.findById(any(Long.class)))
                    .thenReturn(Optional.ofNullable(stock));

            // when
            StockResponseDto result = stockService.getProductStocks(productId);

            // then
            assertThat(result.productId()).isEqualTo(productId);
            assertThat(result.stock()).isEqualTo(expectedStock);
        }

        @Test
        @DisplayName("상품의 재고 조회 시 재고가 없으면 예외 처리")
        void getProductStocks_WhenNoStock_ThrowsException() {
            // given
            Long productId = 1L;

            // mocking
            when(stockRepository.findById(any(Long.class)))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> stockService.getProductStocks(productId))
                    .isInstanceOf(CustomException.class);
        }
    }

    @Nested
    @DisplayName("상품 삭제 요청 테스트")
    class DeleteProductStocksTests{

        @Test
        @DisplayName("상품 삭제 성공")
        void deleteProductStocks_successfully() {
            // given
            Long productId = 1L;
            Long quantity = 100L;
            Stock stock = new Stock(productId, quantity);

            // mocking
            when(stockRepository.findById(any(Long.class)))
                    .thenReturn(Optional.ofNullable(stock));

            // when
            stockService.deleteProductStocks(productId);

            // then
            verify(stockRepository).delete(any(Stock.class));
        }
    }

    @Nested
    @DisplayName("재고 증가 요청 테스트")
    class IncreaseProductStockTests{

        @Test
        @DisplayName("재고 증가 성공")
        void increaseProductStock_successfully() {
            // given
            Long productId = 1L;
            Long initialStock = 100L;
            Long increaseAmount = 50L;
            Long expectedStock = initialStock + increaseAmount;
            Stock stock = new Stock(productId, initialStock);
            StockRequestDto requestDto = new StockRequestDto(productId, increaseAmount);

            // mocking
            when(stockRepository.findById(productId))
                    .thenReturn(Optional.ofNullable(stock));
            when(stockRepository.save(any(Stock.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            StockResponseDto result = stockService.increaseProductStock(requestDto);

            // then
            assertThat(result.productId()).isEqualTo(productId);
            assertThat(result.stock()).isEqualTo(expectedStock);

            // verify
            verify(stockRepository).findById(productId);
            verify(stockRepository).save(any(Stock.class));
        }

        @Test
        @DisplayName("상품 재고 증가 시 상품이 존재하지 않으면 커스텀 예외 발생")
        void increaseProductStock_ProductNotFound_ThrowsException() {
            // given
            Long productId = 1L;
            Long increaseAmount = 50L;
            StockRequestDto requestDto = new StockRequestDto(productId, increaseAmount);

            // mocking
            when(stockRepository.findById(productId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> stockService.increaseProductStock(requestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("상품을 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("재고 증가 요청 시 재고 수량이 음수 또는 0인 경우 예외 처리")
        void increaseProductStock_InvalidStockQuantity_ThrowsException() {
            // given
            Long productId = 1L;
            Long invalidIncreaseAmount = -10L;
            StockRequestDto requestDto = new StockRequestDto(productId, invalidIncreaseAmount);

            // when & then
            assertThatThrownBy(() -> stockService.increaseProductStock(requestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("잘못된 재고 수량입니다.");
        }
    }

    @Nested
    @DisplayName("재고 감소 요청 테스트")
    class decreaseProductStockTests{

        @Test
        @DisplayName("재고 감소 성공")
        void decreaseProductStock_successfully() {
            // given
            Long productId = 1L;
            Long initialStock = 150L;
            Long decreaseAmount = 50L;
            Long expectedStock = initialStock - decreaseAmount;
            Stock stock = new Stock(productId, initialStock);
            StockRequestDto requestDto = new StockRequestDto(productId, decreaseAmount);

            // mocking
            when(stockRepository.findById(productId))
                    .thenReturn(Optional.ofNullable(stock));
            when(stockRepository.save(any(Stock.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            StockResponseDto result = stockService.decreaseProductStock(requestDto);

            // then
            assertThat(result.productId()).isEqualTo(productId);
            assertThat(result.stock()).isEqualTo(expectedStock);
        }

        @Test
        @DisplayName("재고 감소 요청 시 상품이 존재하지 않으면 커스텀 예외 발생")
        void decreaseProductStock_ProductNotFound_ThrowsException() {
            // given
            Long productId = 999L;
            Long decreaseAmount = 50L;
            StockRequestDto requestDto = new StockRequestDto(productId, decreaseAmount);

            // mocking
            when(stockRepository.findById(productId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> stockService.decreaseProductStock(requestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("상품을 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("재고 감소 요청 시 요청된 수량이 현재 재고보다 많으면 커스텀 예외 발생")
        void decreaseProductStock_InsufficientStock_ThrowsException() {
            // given
            Long productId = 1L;
            Long initialStock = 30L;
            Long decreaseAmount = 50L;
            Stock stock = new Stock(productId, initialStock);
            StockRequestDto requestDto = new StockRequestDto(productId, decreaseAmount);

            // mocking
            when(stockRepository.findById(productId))
                    .thenReturn(Optional.ofNullable(stock));

            // when & then
            assertThatThrownBy(() -> stockService.decreaseProductStock(requestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining("요청된 수량이 재고보다 많습니다.");
        }

    }

}