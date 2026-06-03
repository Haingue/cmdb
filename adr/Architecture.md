# CMDB

## 2025-07-08

```plantuml
@startuml

package CMDB {
  package IventoryService {
    database "PostGresql" {
      folder "InventoryDb" as db {
        [Entity]
        [Link]
        [Event]
        [Setting]
      }
    }

    [InventoryService] as inventoryservice
    inventoryservice -> db
  }

  package Cartography {
    [BackendForFrontend] as cartographybff
    [Frontend] as cartographyfrontend
    cartographyfrontend - cartographybff
  }
  cartographybff <-> inventoryservice
}

@enduml
```

## 2025-11-01

```plantuml
@startuml

package CMDB {
  package IventoryService {
    database "PostGresql" {
      folder "InventoryDb" as db {
        [Entity]
        [Link]
        [Event]
        [Setting]
      }
    }

    [InventoryService] as inventoryservice
    inventoryservice -> db
  }

  package Agregator {
    [Syslog API] as syslog
    [Git API] as git
  }
  [Firewall] as networkdevice
  networkdevice -> syslog : syslog (UPD)

  package Cartography {
    [BackendForFrontend] as cartographybff
    [Frontend] as cartographyfrontend
    cartographyfrontend - cartographybff
  }
  cartographybff -> inventoryservice : HTTPS
  syslog -> cartographybff : HTTPS
  git -> cartographybff : HTTPS
}

@enduml
```
