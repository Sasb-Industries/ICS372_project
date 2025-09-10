# TODO

## 1. Project Setup
- [ ] Create Git repository and add team members
- [ ] Configure `.gitignore` (ignore build files, IDE configs, etc.)
- [ ] Set up base project structure (src/, resources/, docs/)
- [ ] Add example JSON input file for testing

## 2. Core Functionality
- [ ] Implement JSON file reader
- [ ] Parse and store order details (ID, type, time, items, qty, price)
- [ ] Support 2 order types: to-go and pickup
- [ ] Implement commands:
  - [ ] Start incoming order (only if not started/completed)
  - [ ] Display incoming order
  - [ ] Complete incoming order (and record it)
- [ ] Export all orders to a single JSON file
- [ ] Show list of uncompleted orders with price totals

## 3. Object-Oriented Design
- [ ] Define Order class (base) with attributes and methods
- [ ] Define ToGoOrder and PickupOrder subclasses (if needed)
- [ ] Implement OrderManager to handle order operations
- [ ] Ensure encapsulation and good design principles

## 4. User Interface (UI/CLI)
- [ ] Build menu-driven interface for commands
- [ ] Validate inputs (order IDs, item data, etc.)
- [ ] Display clear error messages and confirmations

## 5. Documentation
- [ ] Write inline code comments
- [ ] Produce class diagram
- [ ] Produce sequence diagram for "add incoming order"
- [ ] Update README with setup/run instructions

## 6. Testing
- [ ] Unit tests for JSON parsing
- [ ] Unit tests for Order operations (start, display, complete)
- [ ] Integration test with sample input file
- [ ] Test exporting orders to JSON

## 7. Deliverables
- [ ] Zip file with code, libraries (exclude JRE)
- [ ] Link to Git repo
- [ ] Include diagrams in submission
- [ ] Each member writes 1-paragraph reflection (contributions, learning, peer evaluation)

