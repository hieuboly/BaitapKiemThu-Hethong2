package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Hệ thống đặt phòng khách sạn.
 *
 * Ràng buộc đầu vào (độc lập, không ràng buộc chéo):
 *   soDem            : 1..30
 *   soKhach          : 1..4
 *   phanTramGiamGia  : 0..50
 *
 * Công thức: gia = soDem * 500.000 * soKhach * (1 - phanTramGiamGia / 100)
 */
public class DatPhongKhachSan {

    public static final int SO_DEM_MIN = 1;
    public static final int SO_DEM_MAX = 30;
    public static final int SO_KHACH_MIN = 1;
    public static final int SO_KHACH_MAX = 4;
    public static final int GIAM_GIA_MIN = 0;
    public static final int GIAM_GIA_MAX = 50;

    public static final long GIA_MOI_DEM_MOI_KHACH = 500_000L;

    /** Kết quả của một lần đặt phòng. */
    public static final class KetQua {
        private final boolean hopLe;
        private final long gia;
        private final String thongBaoLoi;

        private KetQua(boolean hopLe, long gia, String thongBaoLoi) {
            this.hopLe = hopLe;
            this.gia = gia;
            this.thongBaoLoi = thongBaoLoi;
        }

        static KetQua thanhCong(long gia) {
            return new KetQua(true, gia, null);
        }

        static KetQua loi(String thongBao) {
            return new KetQua(false, 0L, thongBao);
        }

        public boolean isHopLe() {
            return hopLe;
        }

        /** Giá đặt phòng (VNĐ). Chỉ có ý nghĩa khi isHopLe() == true. */
        public long getGia() {
            return gia;
        }

        /** Thông báo lỗi. Null khi đầu vào hợp lệ. */
        public String getThongBaoLoi() {
            return thongBaoLoi;
        }

        @Override
        public String toString() {
            return hopLe ? ("Dat phong thanh cong, gia = " + gia + " VND") : thongBaoLoi;
        }
    }

    // ------------------------------------------------------------------
    // Kiểm tra hợp lệ
    // ------------------------------------------------------------------

    public static boolean soDemHopLe(int soDem) {
        return soDem >= SO_DEM_MIN && soDem <= SO_DEM_MAX;
    }

    public static boolean soKhachHopLe(int soKhach) {
        return soKhach >= SO_KHACH_MIN && soKhach <= SO_KHACH_MAX;
    }

    public static boolean phanTramGiamGiaHopLe(int phanTramGiamGia) {
        return phanTramGiamGia >= GIAM_GIA_MIN && phanTramGiamGia <= GIAM_GIA_MAX;
    }

    // ------------------------------------------------------------------
    // Nghiệp vụ chính
    // ------------------------------------------------------------------

    /**
     * Đặt phòng: kiểm tra đầu vào rồi tính giá.
     *
     * @return KetQua chứa giá nếu hợp lệ, hoặc thông báo lỗi liệt kê
     *         tất cả các đầu vào không hợp lệ.
     */
    public static KetQua datPhong(int soDem, int soKhach, int phanTramGiamGia) {
        List<String> bienSai = new ArrayList<>();
        if (!soDemHopLe(soDem)) {
            bienSai.add("soDem");
        }
        if (!soKhachHopLe(soKhach)) {
            bienSai.add("soKhach");
        }
        if (!phanTramGiamGiaHopLe(phanTramGiamGia)) {
            bienSai.add("phanTramGiamGia");
        }

        if (!bienSai.isEmpty()) {
            return KetQua.loi(taoThongBaoLoi(bienSai));
        }
        return KetQua.thanhCong(tinhGia(soDem, soKhach, phanTramGiamGia));
    }

    /**
     * Tính giá khi đã biết đầu vào hợp lệ.
     *
     * @throws IllegalArgumentException nếu một trong ba đầu vào ngoài miền hợp lệ.
     */
    public static long tinhGia(int soDem, int soKhach, int phanTramGiamGia) {
        if (!soDemHopLe(soDem) || !soKhachHopLe(soKhach) || !phanTramGiamGiaHopLe(phanTramGiamGia)) {
            throw new IllegalArgumentException("Dau vao khong hop le");
        }
        // Nhân trước, chia sau để tránh sai số dấu phẩy động.
        return soDem * GIA_MOI_DEM_MOI_KHACH * soKhach * (100 - phanTramGiamGia) / 100;
    }

    /** Ghép danh sách biến sai thành câu: "a, b và c không hợp lệ". */
    private static String taoThongBaoLoi(List<String> bienSai) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bienSai.size(); i++) {
            if (i > 0) {
                sb.append(i == bienSai.size() - 1 ? " và " : ", ");
            }
            sb.append(bienSai.get(i));
        }
        sb.append(" không hợp lệ");
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Chạy thử nhanh
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println(datPhong(3, 2, 10));   // 2.700.000 VND
        System.out.println(datPhong(1, 1, 0));    // 500.000 VND
        System.out.println(datPhong(30, 4, 50));  // 30.000.000 VND
        System.out.println(datPhong(0, 5, 60));   // loi ca 3 input
    }
}