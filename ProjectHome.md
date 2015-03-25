[JTableAnnotations](http://code.google.com/p/jtableannotations/) is a simple project to help you build [JTable](http://java.sun.com/javase/6/docs/api/javax/swing/JTable.html) easier. It configures and populate your JTable based on annotations.

Basically you must annotated your entities: (there is a lot of [CoC](http://en.wikipedia.org/wiki/Convention_over_configuration))
```
@JTableConfiguration(rowHeight=48)
public class Product {
    @JTableColumnConfiguration(order=1,width=55)
    private Long id;
    @JTableColumnConfiguration(order=2,width=300,editable=true,align=Align.CENTER)
    private String name;
    @JTableColumnConfiguration(order=3,name="Price",decimalFormat="U$ 0.00")
    private BigDecimal value;
    @JTableColumnConfiguration(order=4,name="Costumer Name",width=300)
    private Customer customer;
    @JTableColumnConfiguration(order=0,name="Photo",cellRender=IconCellRender.class)
    private String picturePath;
```
And then you just populate your jtable:
```
            Customer master = new Customer();
            master.setName("Master");
            master.setUserName("Admin");
            list = new ArrayList<Product>();
            list.add(
                    createProduct(
                       master, 0L,
                       "Nintendo Wii",
                       "pictures/wii.jpg",
                       new BigDecimal(999.50))
                                 );
            list.add(
                    createProduct(
                       master, 1L,
                       "XBox360",
                       "pictures/xbox360.jpg",
                       new BigDecimal(1020.85))
                                 );
            list.add(
                    createProduct(
                       master, 2L,
                       "PS3",
                       "pictures/ps3.jpg",
                       new BigDecimal(1000.0))
                                  );
            list.add(
                    createProduct(
                       master, 3L,
                       "PSP",
                       "pictures/psp.jpg",
                       new BigDecimal(490.0))
                                  );
            list.add(
                    createProduct(
                       master, 4L,
                       "Nintendo DS",
                       "pictures/nds.jpg",
                       new BigDecimal(359.59015))
                                 );
            new Configurator().configureAndPopulateJTable(jTable, list);
```
And You got it!
http://jtableannotations.googlecode.com/files/jtable%20annotation%20sample.JPG