import requests
from concurrent.futures import ThreadPoolExecutor
from datetime import datetime

def send_http_request(buyerNum):
    order_url = "http://localhost:8083/orders-service/api/v1/orders"
    payment_url = "http://localhost:8084/payment-service/api/v1/payments"

    try:

    #     # {
    # "userId" : 1,
    # "productId" : 2,
    # "price" : "10000",
    # "quantity" : 1
    # }

        # POST 요청을 보냄(아이템 번호 매번 바꿔야함)
        response = requests.post(order_url, json={"userId": buyerNum, "productId": 3, "price": 1000, "quantity":1})
        # print(f"Request to {order_url} with buyerNum {buyerNum} completed with status code {response.status_code}")

        # 응답을 JSON 형식으로 파싱
        data = response.json()

#     {
#     "status": "CREATED",
#     "message": "일반 상품 주문 생성 성공",
#     "data": {
#         "orderId": 10,
#         "userId": 1,
#         "productId": 3,
#         "price": 44400.00,
#         "quantity": 1,
#         "ordersType": "IN_PROGRESS"
#     }
#   }

        # "orderId" 키가 있는지 확인
        if "orderId" in data.get("data", {}):
            orderId = data["data"]["orderId"]
            print(f"Received orderId: {orderId}")

            # 결제 요청을 보냄
            payment_response = requests.post(payment_url, json={"orderId": orderId})

            if payment_response.status_code == 201:
                payment_data = payment_response.json()
                print(f"Payment for orderId {orderId} completed successfully, {payment_data['data']}")
            else:
                payment_data = payment_response.json()
                print(f"Payment for orderId {orderId} failed with status code {payment_data}")
        # else:
        #     print("Response does not contain 'orderNum' key")

    except requests.exceptions.RequestException as e:
        print(f"Error sending request to {order_url}: {e}")

def main():
    # Set the number of concurrent requests (N)
    num_requests = 100  # buyerNum을 1부터 10000까지 보낼 것이므로 요청 수를 10000으로 설정

    start_time = datetime.now()  # 코드 실행 시작 시간

    # Create a ThreadPoolExecutor to send concurrent requests
    with ThreadPoolExecutor(max_workers=10) as executor:
        # Use a list comprehension to create a list of tasks
        tasks = [executor.submit(send_http_request, buyerNum) for buyerNum in range(1, num_requests + 1)]

        # Wait for all tasks to complete
        for future in tasks:
            future.result()

    end_time = datetime.now()  # 코드 실행 종료 시간
    duration = end_time - start_time

    print(f"실행 소요시간 {duration}")

if __name__ == "__main__":
    main()