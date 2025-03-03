import hinhhoc.HinhVuong;
import hinhhoc.HinhTron;

public class test {
    public static void main(String[] args) {
        // Tạo đối tượng HinhVuong
        HinhVuong hv = new HinhVuong(5.0);
        System.out.println("Diện tích hình vuông: " + hv.tinhDienTich());
        System.out.println("Chu vi hình vuông: " + hv.tinhChuVi());

        // Tạo đối tượng HinhTron
        HinhTron ht = new HinhTron(3.0);
        System.out.println("Diện tích hình tròn: " + ht.tinhDienTich());
        System.out.println("Chu vi hình tròn: " + ht.tinhChuVi());
    }
}