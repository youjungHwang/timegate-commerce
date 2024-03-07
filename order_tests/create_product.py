import pymysql
from datetime import datetime


# 테이블 초기화 함수
def truncate_table(connection, table_name):
    cursor = connection.cursor()
    cursor.execute(f"TRUNCATE TABLE {table_name}")
    connection.commit()
    print(f"{table_name} db 초기화 성공")
    cursor.close()

def insert_products():
    try:
        # 데이터베이스 연결 설정
        connection = pymysql.connect(
            host='localhost',
            port=13310,
            database='product_database',
            user='youjung',
            password='timegate2249207'
        )
        
        # product 테이블 초기화
        truncate_table(connection, 'product')

        cursor = connection.cursor()

        # 일반 상품 등록
        normal_product_query = """
        INSERT INTO product (product_name, price, product_type, available_from, available_until) 
        VALUES (%s, %s, %s, %s, %s)
        """
        normal_product_values = ("일반 상품", 12500, "REGULAR", None, None)
        cursor.execute(normal_product_query, normal_product_values)
        normal_product_id = cursor.lastrowid

        # 예약 상품 등록
        pre_order_product_query = """
        INSERT INTO product (product_name, price, product_type, available_from, available_until) 
        VALUES (%s, %s, %s, %s, %s)
        """
        available_at = datetime(2024, 2, 22, 7, 0, 0)
        end_at = datetime(2025, 2, 22, 7, 0, 0)
        pre_order_product_values = ('예약 상품', 500, 'RESERVED', available_at, end_at)
        cursor.execute(pre_order_product_query, pre_order_product_values)
        pre_order_product_id = cursor.lastrowid

        connection.commit()

        print(f"일반 상품과 예약 상품이 성공적으로 등록되었습니다. 상품 번호: {normal_product_id}, {pre_order_product_id}")

    except pymysql.MySQLError as e:
        print("데이터베이스 연결 또는 쿼리 실행 중 오류 발생:", e)
    finally:
        # 연결 종료
        cursor.close()
        connection.close()
        print("MySQL 연결이 종료됨.")
    return [normal_product_id, pre_order_product_id]

def insert_stocks(firstProductId, secondProductId):
    try:
        # 데이터베이스 연결 설정
        connection = pymysql.connect(
            host='localhost',
            port=13313,
            database='stock_database',
            user='youjung',
            password='timegate2249207'
        )

        # stock 테이블 초기화
        truncate_table(connection, 'stock')
        
        cursor = connection.cursor()

        # 재고 등록
        stock_query = "INSERT INTO stock (product_id, stock) VALUES (%s, %s)"
        cursor.execute(stock_query, (firstProductId, 100000))

        retrieve_query = "SELECT * FROM stock WHERE product_id = %s"
        cursor.execute(retrieve_query, (firstProductId,))
        inserted_data = cursor.fetchone()
        print(f"삽입된 데이터: {inserted_data}")

        cursor.execute(stock_query, (secondProductId, 100000))
        cursor.execute(retrieve_query, (secondProductId,))
        inserted_data = cursor.fetchone()
        print(f"삽입된 데이터: {inserted_data}")

        connection.commit()

    except pymysql.MySQLError as e:
        print("데이터베이스 연결 또는 쿼리 실행 중 오류 발생:", e)
    finally:
        # 연결 종료
        cursor.close()
        connection.close()
        print("MySQL 연결이 종료됨.")

if __name__ == "__main__":
    insert_product_result = insert_products()
    insert_stocks(insert_product_result[0], insert_product_result[1])
