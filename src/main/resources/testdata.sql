CREATE TABLE IF NOT EXISTS address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    country VARCHAR(255),
    postalCode VARCHAR(255),
    city VARCHAR(255),
    street VARCHAR(255),
    home_no VARCHAR(255),
    flat_no VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    telephone VARCHAR(255),
    address_id INT,
    basic_gross_salary DECIMAL(10, 2),
    date_of_employment DATE,
    form_of_employment VARCHAR(50),
    contract_expiry_date DATE
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);

CREATE TABLE IF NOT EXISTS employee_role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS employees_roles (
    employee_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (employee_id, role_id),
    FOREIGN KEY (employee_id) REFERENCES employees(id),
    FOREIGN KEY (role_id) REFERENCES employee_role(id)
);

CREATE TABLE IF NOT EXISTS forwarders (
    id INT PRIMARY KEY,
    extra_percentage DOUBLE,
    total_margin DECIMAL(10, 2),
    FOREIGN KEY (id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS transportPlanners (
    id INT PRIMARY KEY,
    salary_bonus DOUBLE,
    FOREIGN KEY (id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS accountants (
    id INT PRIMARY KEY,
    type_of_role VARCHAR(50),
    FOREIGN KEY (id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS trucks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    brand VARCHAR(255),
    model VARCHAR(255),
    type VARCHAR(255),
    plates VARCHAR(255),
    technical_inspection_date DATE,
    insurance_date DATE,
    assigned_to_driver BOOLEAN,
    transport_planner_id INT,
    FOREIGN KEY (transport_planner_id) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS drivers (
    id INT PRIMARY KEY,
    driver_licence_number VARCHAR(255),
    licence_expiry_date VARCHAR(255),
    work_system VARCHAR(255),
    truck_id INT,
    assigned_to_truck BOOLEAN,
    main_driver BOOLEAN,
    FOREIGN KEY (id) REFERENCES employees(id),
    FOREIGN KEY (truck_id) REFERENCES trucks(id)
);

CREATE TABLE IF NOT EXISTS warehouse_workers (
    id INT PRIMARY KEY,
    permissions_forklift BOOLEAN,
    permissions_crane BOOLEAN,
    FOREIGN KEY (id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS contractors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255),
    short_name VARCHAR(255),
    address_id INT,
    vat_number VARCHAR(255),
    description VARCHAR(255),
    term_of_payment INT,
    balance DECIMAL(10, 2),
    FOREIGN KEY (address_id) REFERENCES address(id)
);

CREATE TABLE IF NOT EXISTS carriers (
    id INT PRIMARY KEY,
    insurance_expiration_date DATE,
    licence_expiration_date DATE,
    FOREIGN KEY (id) REFERENCES contractors(id)
);

CREATE TABLE IF NOT EXISTS customers (
    id INT PRIMARY KEY,
    payment_rating VARCHAR(255),
    FOREIGN KEY (id) REFERENCES contractors(id)
);

CREATE TABLE IF NOT EXISTS contact_persons (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    telephone VARCHAR(255),
    contractor_id INT,
    FOREIGN KEY (contractor_id) REFERENCES contractors(id)
);


CREATE TABLE IF NOT EXISTS cargos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cargo_number VARCHAR(255),
    price DECIMAL(10, 2),
    currency VARCHAR(255),
    date_added DATE,
    loading_date DATE,
    unloading_date DATE,
    loading_address VARCHAR(255),
    unloading_address VARCHAR(255),
    goods VARCHAR(255),
    description VARCHAR(255),
    assigned_to_order BOOLEAN,
    invoiced BOOLEAN,
    customer_id INT,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS warehouses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type_of_goods VARCHAR(255),
    address_id INT,
    crane BOOLEAN,
    forklift BOOLEAN,
    cost_per_100_square_meters DOUBLE,
    available_area DOUBLE,
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);

CREATE TABLE IF NOT EXISTS goods (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    type_of_goods VARCHAR(255),
    quantity DOUBLE,
    label VARCHAR(255),
    description VARCHAR(255),
    required_area DOUBLE,
    assigned_to_order BOOLEAN
);

CREATE TABLE IF NOT EXISTS warehouse_orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id INT,
    customer_id INT,
    date_added DATE,
    date_of_return DATE,
    total_costs DOUBLE,
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    FOREIGN KEY (customer_id) REFERENCES contractors(id)
);

CREATE TABLE IF NOT EXISTS warehouse_order_goods (
    warehouse_order_id INT,
    goods_id INT,
    PRIMARY KEY (warehouse_order_id, goods_id),
    FOREIGN KEY (warehouse_order_id) REFERENCES warehouse_orders(id),
    FOREIGN KEY (goods_id) REFERENCES goods(id)
);

INSERT INTO employee_role (name, description)
VALUES ('FORWARDER', 'FORWARDER, ACCESS ONLY TO FORWARDER PANEL'),
       ('PLANNER', 'PLANER, ACCESS ONLY TO PLANNER PANEL'),
       ('ACCOUNTANT', 'ACCOUNTANT, ACCESS ONLY TO ACCOUNTANT PANEL'),
       ('ADMIN', 'ADMIN, ACCESS TO ALL PANELS');

INSERT INTO addresses (country, postal_code, city, street, home_no, flat_no)
VALUES ('Poland', '00-000', 'Warszawa', 'ul. Nowa', '1', '44'),
       ('Poland', '80-000', 'Gdańsk', 'ul. Stara', '5', '2'),
       ('Poland', '60-000', 'Poznań', 'ul. Testowa', '10', '1A'),
       ('Poland', '70-000', 'Szczecin', 'ul. Długa', '3', '70'),
       ('Poland', '60-000', 'Poznań', 'ul. Krótka', '5', '5'),
       ('Poland', '30-000', 'Kraków', 'ul. Szeroka', '5', '5');

INSERT INTO employees (first_name, last_name, email, password, telephone, address_id, basic_gross_salary, date_of_employment, form_of_employment, contract_expiry_date)
VALUES ('John', 'Doe', 'john@example.com', '{bcrypt}$2a$10$ekPcaMnn28t7GP7GSGcF3uhe2xx3YVMr.FrC1B/kUkzw2/UM0Anm2', '+123456789', 1, 10000.00, '2023-01-01', 'CONTRACT_OF_EMPLOYMENT', null),
       ('Forwarder', 'Forwarder', 'forwarder@example.com', '{bcrypt}$2a$10$ekPcaMnn28t7GP7GSGcF3uhe2xx3YVMr.FrC1B/kUkzw2/UM0Anm2', '+123456789', 2, 5000.00, '2023-01-01', 'CONTRACT_OF_EMPLOYMENT', null),
       ('Forwarder2', 'Forwarder', 'forwarder2@example.com', '{bcrypt}$2a$10$ekPcaMnn28t7GP7GSGcF3uhe2xx3YVMr.FrC1B/kUkzw2/UM0Anm2', '+123456789', 3, 5000.00, '2023-01-01', 'CONTRACT_OF_EMPLOYMENT', null),
       ('Accountant', 'Accountant', 'acc@example.com', '{bcrypt}$2a$10$ekPcaMnn28t7GP7GSGcF3uhe2xx3YVMr.FrC1B/kUkzw2/UM0Anm2', '+123456789', 4, 3000.00, '2023-01-01', 'CONTRACT_OF_EMPLOYMENT', null);

INSERT INTO employees_roles (employee_id, role_id)
VALUES (1, 4),
       (2, 1),
       (3, 1),
       (4, 3);
INSERT INTO forwarders (id, extra_percentage)
VALUES (2, 20.0),
       (3, 20.0);

INSERT INTO accountants(id, type_of_role)
VALUES (4, 'INVOICES');

INSERT INTO contractor (full_name, short_name, address_id, vat_number, description, term_of_payment, balance)
VALUES ('ABC Spedition Poland', 'ABC Spedition', 5, 'PL1234567890', 'Test', 30, 0.00),
       ('Polska Grupa Transportowa', 'PGT', 6, 'PL1234567890', 'Test', 60, 0.00);

INSERT INTO customer (id, payments_rating)
VALUES (1, 'SMALL_DELAYS');

INSERT INTO carrier (id,insurance_expiration_date, licence_expiration_date)
VALUES (2, '2024-12-31', '2024-12-31');
