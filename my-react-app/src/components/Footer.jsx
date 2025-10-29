import React from 'react';
import { Link } from 'react-router-dom';

function Footer() {
  return (
    <footer className="bg-dark text-white py-4">
      <div className="container">
        <div className="row">
          <div className="col-lg-6">
            <h5 className="fw-bold mb-3">
              <i className="fas fa-building me-2"></i>
              HR Sphere
            </h5>
            <p className="mb-3">
              Empowering organizations with comprehensive human resource management 
              solutions for the modern workplace.
            </p>
            <div className="d-flex gap-3">
              <a href="https://www.facebook.com/" className="text-white">
                <i className="fab fa-facebook fa-lg"></i>
              </a>
              <a href="http://x.com/" className="text-white">
                <i className="fab fa-twitter fa-lg"></i>
              </a>
              <a href="https://www.linkedin.com/" className="text-white">
                <i className="fab fa-linkedin fa-lg"></i>
              </a>
              <a href="https://www.instagram.com/" className="text-white">
                <i className="fab fa-instagram fa-lg"></i>
              </a>
            </div>
          </div>
          <div className="col-lg-3">
            <h6 className="fw-bold mb-3">Quick Links</h6>
            <ul className="list-unstyled">
              <li><Link to="/" className="text-white-50 text-decoration-none">Home</Link></li>
              <li><Link to="/about" className="text-white-50 text-decoration-none">About Us</Link></li>
              <li><Link to="/login" className="text-white-50 text-decoration-none">Login</Link></li>
            </ul>
          </div>
          <div className="col-lg-3">
            <h6 className="fw-bold mb-3">Contact Info</h6>
            <p className="text-white-50 mb-1">
              <i className="fas fa-envelope me-2"></i>
              info@hrsphere.com
            </p>
            <p className="text-white-50 mb-1">
              <i className="fas fa-phone me-2"></i>
              +91 9876543210
            </p>
            <p className="text-white-50">
              <i className="fas fa-map-marker-alt me-2"></i>
              India
            </p>
          </div>
        </div>
        <hr className="my-4" />
        <div className="text-center">
          <p className="mb-0 text-white-50">
            © 2025 HR Work Sphere. All rights reserved. | Built with ❤️ for better HR Work
          </p>
        </div>
      </div>
    </footer>
  );
}

export default Footer;
