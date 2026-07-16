/*
En este paquete MapeoPojos tenemos lo que seria equivalente a nuestras tablas en MySQL es decir:
    la tabla Estudiante y Universidad que seria la clase Estudiante.java y Universidad.java
    Esto es parte del concepto de mapeo objeto-relacional (ORM) que usa Hibernate.
        Clases Java (Pojos)
            Cada clase Java en MapeoPojos corresponde a una tabla en la bbdd
            los atributos de la clase corresponden a las columnas de la tabla 
                Ejemplo:
                Clase Universidad → Tabla universidad.
                Atributo codigo → Columna codigo en la tabla.
        Archivos de mapeo ( .hbm.xml )
            Cada clase Java tiene un archivo .hbm.xml que describe cómo se relaciona con la tabla (mapa entre POJO y tabla).
            En el caso de la clase Universidad, el archivo Universidad.hbm.xml indica que:
                La tabla asociada es universidad.
                La columna codigo en la tabla se corresponde con el atributo codigo en la clase.
        Hibernate gestiona la persistencia:
            Usando estas clases, Hibernate se encarga de convertir los objetos en filas de la tabla y viceversa.
        
    


 */
