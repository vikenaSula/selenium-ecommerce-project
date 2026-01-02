# Selenium Test Automation Project

## Overview
This project contains automated test cases for the **Tealium E-commerce Demo** website (https://ecommerce.tealiumdemo.com/) using Selenium WebDriver with Java and TestNG framework.

## Project Structure
```
Selenium Project/
├── src/
│   └── main/
│       └── java/
│           ├── pages/           # Page Object Model classes
│           │   ├── BasePage.java
│           │   ├── HomePage.java
│           │   ├── WomenProductsPage.java
│           │   ├── MenProductsPage.java
│           │   ├── SaleProductsPage.java
│           │   └── ShoppingCartPage.java
│           ├── tests/           # Test classes
│           │   ├── ProductHoverEffectTest.java
│           │   ├── SaleProductsStyleTest.java
│           │   ├── MenProductsFilterTest.java
│           │   ├── SortingAndWishlistTest.java
│           │   └── WishlistToCartTest.java
│           └── utils/          
├── screenshots/                 # Test failure screenshots
├── pom.xml                     # Maven dependencies
├── testng.xml                  # TestNG suite configuration
└── README.md                  
```

## Technologies Used
- **Java** (JDK 25)
- **Selenium WebDriver** (4.27.0)
- **TestNG** (7.10.2)
- **WebDriverManager** (5.9.2) - Automatic driver management
- **Maven** - Build and dependency management
- **Page Object Model (POM)** - Design pattern for test automation

## Prerequisites
- Java JDK 25 or higher
- Maven 3.6 or higher
- Chrome browser (latest version)
- IntelliJ IDEA (recommended) or any Java IDE

## Setup Instructions

### 1. Clone or Download the Project
```bash
git clone <repository-url>
cd "Selenium Project"
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Configure Chrome Driver
The project uses WebDriverManager which automatically downloads and configures ChromeDriver. No manual setup required!

## Test Cases

### Test 1: Product Hover Effect Test
**File:** `ProductHoverEffectTest.java`

**Description:** Verifies that product hover effects work correctly on the Women's products page.

**Test Steps:**
1. Hover over "WOMEN" in the navigation menu
2. Click "View All Woman" option
3. Hover over a product on the page
4. Assert that CSS styles change to indicate a hover effect (box-shadow, border, transform)

**Validation:**
- ✅ At least one CSS property changes on hover
- ✅ Screenshot captured on failure

---

### Test 2: Sale Products Style Test
**File:** `SaleProductsStyleTest.java`

**Description:** Verifies pricing and styling for products on the Sale page.

**Test Steps:**
1. Hover over "SALE" in the navigation menu
2. Click "View All Sale" option
3. For each sale product, verify:
   - Multiple prices are shown (original + discounted)
   - Original price has grey color (`rgba(160, 160, 160, 1)`)
   - Original price has strikethrough text-decoration
   - Final price is blue (`rgba(51, 153, 204, 1)`)
   - Final price does NOT have strikethrough

**Validation:**
- ✅ All sale products display both original and discounted prices
- ✅ Price styling matches specifications
- ✅ Detailed console output with product count

---

### Test 3: Men Products Filter Test
**File:** `MenProductsFilterTest.java`

**Description:** Tests color and price filtering functionality on the Men's products page.

**Test Steps:**
1. Hover over "MEN" and click "View All Men"
2. Click on **Black** color filter
3. Verify all displayed products have black color option with blue border (indicating selection)
4. Click on **Price** dropdown and select first option ($0.00 - $99.99)
5. Verify exactly 3 products are displayed
6. Verify each product price matches the selected criteria ($0.00 - $99.99)

**Validation:**
- ✅ Color filter applies correctly (blue border on selected color)
- ✅ Price filter shows correct number of products (3 products)
- ✅ All product prices are within the selected range

---

### Test 4: Sorting and Wishlist Test
**File:** `SortingAndWishlistTest.java`

**Description:** Tests product sorting by price and wishlist functionality.

**Test Steps:**
1. Hover over "WOMEN" and click "View All Woman"
2. Click on "Sort By" dropdown and select "Price"
3. Verify products are sorted by price in ascending order
4. Add first two products to wishlist
5. Verify wishlist counter shows "My Wish List (2 items)"

**Validation:**
- ✅ Products sorted correctly by price (ascending)
- ✅ Wishlist count displays 2 items

---


Running the Tests

### Run All Tests (TestNG Suite)
```bash
mvn test
```

Or right-click `testng.xml` in your IDE and select **Run**

### Run Individual Test
```bash
mvn test -Dtest=ProductHoverEffectTest
mvn test -Dtest=SaleProductsStyleTest
mvn test -Dtest=MenProductsFilterTest
mvn test -Dtest=SortingAndWishlistTest
mvn test -Dtest=WishlistToCartTest
```

**Last Updated:** January 3, 2026

