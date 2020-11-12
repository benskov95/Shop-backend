package facades;

import dto.ProductDTO;
import entities.Product;
import errorhandling.AlreadyExists;
import errorhandling.MissingInput;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

public class ProductFacade {
    
    private static EntityManagerFactory emf;
    private static ProductFacade instance;

    private ProductFacade() {
    }

    public static ProductFacade getProductFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ProductFacade();
        }
        return instance;
    }
    
    public List<ProductDTO> getAllProducts() {
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT p FROM Product p");
        List<Product> products = q.getResultList();
        
        List<ProductDTO> productDTOs = new ArrayList<>();
        for (Product p : products) {
            productDTOs.add(new ProductDTO(p));
        }
        return productDTOs;
    }
    
    public ProductDTO getProductById(int id) {
        EntityManager em = emf.createEntityManager();
        Product product = em.find(Product.class, id);
        return new ProductDTO(product);
    }
    
    public ProductDTO addProduct(ProductDTO pDTO) throws AlreadyExists, MissingInput {
        EntityManager em = emf.createEntityManager();
        Product product = new Product();
        prepareProduct(pDTO, product);
        checkInput(pDTO);
        checkIfProductExists(product, em);
        
        try {
            em.getTransaction().begin();
            em.persist(product);
            em.getTransaction().commit();
            return new ProductDTO(product);
        } finally {
            em.close();
        }
    }
    
    public ProductDTO deleteProduct(int id) throws AlreadyExists {
        EntityManager em = emf.createEntityManager();
        Product product = em.find(Product.class, id);
        isProductInUse(product.getId(), em);
        
        try {
            em.getTransaction().begin();
            em.remove(product);
            em.getTransaction().commit();
            return new ProductDTO(product);
        } finally {
            em.close();
        }     
    }
    
    public ProductDTO editProduct(ProductDTO pDTO) throws AlreadyExists, MissingInput {
        EntityManager em = emf.createEntityManager();
        Product product = em.find(Product.class, pDTO.getProductId());
        checkInput(pDTO);
        prepareProduct(pDTO, product);
        checkIfProductExists(product, em);
        
        try {
            em.getTransaction().begin();
            em.persist(product);
            em.getTransaction().commit();
            return new ProductDTO(product);
        } finally {
            em.close();
        }
    }
    
    private void prepareProduct(ProductDTO pDTO, Product p) {
        p.setTitle(pDTO.getTitle());
        p.setPrice(pDTO.getPrice());
        p.setDescription(pDTO.getDescription());
        p.setCategory(pDTO.getCategory());
        p.setImage(pDTO.getImage());
    }
    
    private void checkIfProductExists(Product p, EntityManager em) throws AlreadyExists {
        Query q = em.createQuery("SELECT p FROM Product p WHERE p.title = :title");
        q.setParameter("title", p.getTitle());
        if (q.getResultList().size() > 0) {
            throw new AlreadyExists("This product already exists in the database.");
        }
    }
    
    private void checkInput(ProductDTO pDTO) throws MissingInput {
        if (pDTO.getTitle().length() < 3 ||
            pDTO.getPrice() < 0.1 ||
            pDTO.getDescription().length() < 5 ||
            pDTO.getCategory().length() < 3 ||
            pDTO.getImage().length() < 5) 
        {
            throw new MissingInput("All fields must be filled out.");
        }
    }
    
    private void isProductInUse(int id, EntityManager em) throws AlreadyExists {
        Query q = em.createQuery("SELECT p FROM ProductOrderline p WHERE p.product.id = :id");
        q.setParameter("id", id);
        if (q.getResultList().size() > 0) {
            throw new AlreadyExists("This product cannot be deleted because it is part of an existing order.");
        }
    } 
    
}
