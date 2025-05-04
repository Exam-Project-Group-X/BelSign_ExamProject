package easv.dk.belsign;

import easv.dk.belsign.dal.web.ProductPhotosDAO;

public class TestInsertPhotos {
    public static void main(String[] args) throws Exception {
        ProductPhotosDAO dao = new ProductPhotosDAO();
        dao.insertAllTestPhotos();
        System.out.println("All test photos successfully in DATABASE");
    }

}
