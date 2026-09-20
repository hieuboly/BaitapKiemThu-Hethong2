package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Kiểm thử hệ thống đặt phòng khách sạn.
 *
 * Kỹ thuật: Boundary Value Analysis (BVA) chuẩn, n = 3 biến → 4n + 1 = 13 test case (TC1..TC13),
 * bổ sung 7 test case ngoài biên (TC14..TC20).
 */
@DisplayName("Đặt phòng khách sạn")
class DatPhongKhachSanTest {

    @Nested
    @DisplayName("BVA - đầu vào hợp lệ (TC1..TC13)")
    class BienHopLe {

        @ParameterizedTest(name = "{4}: soDem={0}, soKhach={1}, giam={2}% -> {3} VND")
        @CsvSource({
                "  1, 3, 25,  1125000, TC1  min cua soDem",
                "  2, 3, 25,  2250000, TC2  min+ cua soDem",
                " 29, 3, 25, 32625000, TC3  max- cua soDem",
                " 30, 3, 25, 33750000, TC4  max cua soDem",
                " 15, 1, 25,  5625000, TC5  min cua soKhach",
                " 15, 2, 25, 11250000, TC6  min+ cua soKhach",
                " 15, 3, 25, 16875000, TC7  max- cua soKhach",
                " 15, 4, 25, 22500000, TC8  max cua soKhach",
                " 15, 3,  0, 22500000, TC9  min cua phanTramGiamGia",
                " 15, 3,  1, 22275000, TC10 min+ cua phanTramGiamGia",
                " 15, 3, 49, 11475000, TC11 max- cua phanTramGiamGia",
                " 15, 3, 50, 11250000, TC12 max cua phanTramGiamGia",
                " 15, 3, 25, 16875000, TC13 tat ca normal"
        })
        void datPhongThanhCong(int soDem, int soKhach, int giam, long giaMongDoi, String moTa) {
            DatPhongKhachSan.KetQua kq = DatPhongKhachSan.datPhong(soDem, soKhach, giam);

            assertTrue(kq.isHopLe(), moTa + " phai hop le");
            assertEquals(giaMongDoi, kq.getGia(), moTa);
            assertEquals(giaMongDoi, DatPhongKhachSan.tinhGia(soDem, soKhach, giam), moTa);
        }
    }

    @Nested
    @DisplayName("Ngoài biên - đầu vào không hợp lệ (TC14..TC20)")
    class NgoaiBien {

        @ParameterizedTest(name = "{4}: soDem={0}, soKhach={1}, giam={2}% -> \"{3}\"")
        @CsvSource({
                "  0, 3,  25, 'soDem không hợp lệ',                                  TC14 soDem duoi bien",
                " 31, 3,  25, 'soDem không hợp lệ',                                  TC15 soDem tren bien",
                " 15, 0,  25, 'soKhach không hợp lệ',                                TC16 soKhach duoi bien",
                " 15, 5,  25, 'soKhach không hợp lệ',                                TC17 soKhach tren bien",
                " 15, 3,  -1, 'phanTramGiamGia không hợp lệ',                        TC18 giam gia duoi bien",
                " 15, 3,  51, 'phanTramGiamGia không hợp lệ',                        TC19 giam gia tren bien",
                "  0, 5,  60, 'soDem, soKhach và phanTramGiamGia không hợp lệ',      TC20 ca 3 input sai"
        })
        void datPhongThatBai(int soDem, int soKhach, int giam, String loiMongDoi, String moTa) {
            DatPhongKhachSan.KetQua kq = DatPhongKhachSan.datPhong(soDem, soKhach, giam);

            assertFalse(kq.isHopLe(), moTa + " phai khong hop le");
            assertEquals(loiMongDoi, kq.getThongBaoLoi(), moTa);
            assertEquals(0L, kq.getGia(), "Khong tra ve gia khi input sai");
        }

        @Test
        @DisplayName("Hai input sai cùng lúc -> liệt kê cả hai")
        void haiInputSai() {
            assertEquals("soDem và soKhach không hợp lệ",
                    DatPhongKhachSan.datPhong(0, 5, 25).getThongBaoLoi());
            assertEquals("soDem và phanTramGiamGia không hợp lệ",
                    DatPhongKhachSan.datPhong(31, 3, 51).getThongBaoLoi());
            assertEquals("soKhach và phanTramGiamGia không hợp lệ",
                    DatPhongKhachSan.datPhong(15, 5, -1).getThongBaoLoi());
        }

        @Test
        @DisplayName("tinhGia() ném IllegalArgumentException khi input ngoài miền")
        void tinhGiaNemLoi() {
            assertThrows(IllegalArgumentException.class, () -> DatPhongKhachSan.tinhGia(0, 3, 25));
            assertThrows(IllegalArgumentException.class, () -> DatPhongKhachSan.tinhGia(15, 5, 25));
            assertThrows(IllegalArgumentException.class, () -> DatPhongKhachSan.tinhGia(15, 3, 51));
        }
    }

    @Nested
    @DisplayName("Ví dụ trong đặc tả (mục 6)")
    class ViDuDacTa {

        @ParameterizedTest(name = "soDem={0}, soKhach={1}, giam={2}% -> {3} VND")
        @CsvSource({
                " 3, 2, 10,  2700000",
                " 1, 1,  0,   500000",
                "30, 4, 50, 30000000"
        })
        void viDuHopLe(int soDem, int soKhach, int giam, long giaMongDoi) {
            assertEquals(giaMongDoi, DatPhongKhachSan.datPhong(soDem, soKhach, giam).getGia());
        }

        @Test
        @DisplayName("Ví dụ không hợp lệ")
        void viDuKhongHopLe() {
            assertEquals("soDem không hợp lệ", DatPhongKhachSan.datPhong(0, 2, 10).getThongBaoLoi());
            assertEquals("soKhach không hợp lệ", DatPhongKhachSan.datPhong(3, 6, 10).getThongBaoLoi());
        }
    }

    @Nested
    @DisplayName("Hàm kiểm tra hợp lệ từng biến")
    class KiemTraTungBien {

        @Test
        void bienSoDem() {
            assertFalse(DatPhongKhachSan.soDemHopLe(0));
            assertTrue(DatPhongKhachSan.soDemHopLe(1));
            assertTrue(DatPhongKhachSan.soDemHopLe(30));
            assertFalse(DatPhongKhachSan.soDemHopLe(31));
        }

        @Test
        void bienSoKhach() {
            assertFalse(DatPhongKhachSan.soKhachHopLe(0));
            assertTrue(DatPhongKhachSan.soKhachHopLe(1));
            assertTrue(DatPhongKhachSan.soKhachHopLe(4));
            assertFalse(DatPhongKhachSan.soKhachHopLe(5));
        }

        @Test
        void bienPhanTramGiamGia() {
            assertFalse(DatPhongKhachSan.phanTramGiamGiaHopLe(-1));
            assertTrue(DatPhongKhachSan.phanTramGiamGiaHopLe(0));
            assertTrue(DatPhongKhachSan.phanTramGiamGiaHopLe(50));
            assertFalse(DatPhongKhachSan.phanTramGiamGiaHopLe(51));
        }
    }
}