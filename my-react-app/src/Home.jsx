import React from 'react';
import { Link } from 'react-router-dom';
import Header from './components/Header';
import Footer from './components/Footer';

function Home() {
  return (
    <div className="home-page">
      {/* Header */}
      <Header />

      {/* Hero Section */}
      <section className="hero-section bg-gradient-primary text-white py-5">
        <div className="container">
          <div className="row align-items-center g-4">
            <div className="col-lg-6 order-2 order-lg-1">
              <div className="hero-text-content">
                <h1 className="display-4 fw-bold mb-4 hero-title">
                  Welcome to Human Resource Work Sphere
                </h1>
                <p className="lead mb-4">
                  Streamline your human resource management with our comprehensive 
                  HR portal. Manage employees, track leave requests, handle payroll, 
                  and much more - all in one place.
                </p>
                <div className="d-flex flex-wrap gap-3">
                  <Link to="/login" className="btn btn-light btn-lg">
                    <i className="fas fa-sign-in-alt me-2"></i>
                    Get Started
                  </Link>
                  <Link to="/about" className="btn btn-outline-light btn-lg">
                    <i className="fas fa-info-circle me-2"></i>
                    Learn More
                  </Link>
                </div>
              </div>
            </div>
            <div className="col-lg-6 order-1 order-lg-2">
              <div className="hero-image-wrapper">
                <img 
                  src="/assets/img/hr-hero.jpg" 
                  alt="HR Management" 
                  className="img-fluid rounded shadow-lg hero-image"
                  onError={(e) => {
                    e.target.src = "/assets/img/default.png";
                  }}
                />
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Gallery */}
      <section className="py-5 bg-light">
        <div className="container">
          <div className="text-center mb-5">
            <h2 className="display-5 fw-bold text-primary mb-3">
              Our HR Work Sphere Solutions
            </h2>
            <p className="lead text-muted">
              Discover the power of modern HR Work Sphere with our feature-rich platform
            </p>
          </div>
          
          <div className="row g-4">
            {/* Feature 1 - Employee Management */}
            <div className="col-lg-4 col-md-6">
              <div className="card h-100 shadow-sm hover-shadow">
                <img 
                  src="/assets/img/EM.jpg" 
                  className="card-img-top feature-image" 
                  alt="Employee Management"
                  onError={(e) => {
                    e.target.src = "https://via.placeholder.com/400x250/28a745/ffffff?text=Employee+Management";
                  }}
                />
                <div className="card-body">
                  <h5 className="card-title text-primary">
                    <i className="fas fa-users me-2"></i>
                    Employee Management
                  </h5>
                  <p className="card-text">
                    Efficiently manage your workforce with comprehensive employee profiles, 
                    department organization, and role management capabilities.
                  </p>
                </div>
              </div>
            </div>

            {/* Feature 2 - Leave Management */}
            <div className="col-lg-4 col-md-6">
              <div className="card h-100 shadow-sm hover-shadow">
                <img 
                  src="/assets/img/LM.jpg" 
                  className="card-img-top feature-image" 
                  alt="Leave Management"
                  onError={(e) => {
                    e.target.src = "https://via.placeholder.com/400x250/17a2b8/ffffff?text=Leave+Management";
                  }}
                />
                <div className="card-body">
                  <h5 className="card-title text-info">
                    <i className="fas fa-calendar-alt me-2"></i>
                    Leave Management
                  </h5>
                  <p className="card-text">
                    Streamline leave requests, approvals, and tracking with our 
                    intuitive calendar system and automated workflow processes.
                  </p>
                </div>
              </div>
            </div>

            {/* Feature 3 - Payroll System */}
            <div className="col-lg-4 col-md-6">
              <div className="card h-100 shadow-sm hover-shadow">
                <img 
                  src="/assets/img/PM.jpg" 
                  className="card-img-top feature-image" 
                  alt="Payroll Management"
                  onError={(e) => {
                    e.target.src = "https://via.placeholder.com/400x250/ffc107/000000?text=Payroll+System";
                  }}
                />
                <div className="card-body">
                  <h5 className="card-title text-warning">
                    <i className="fas fa-rupee-sign me-2"></i>
                    Payroll Management
                  </h5>
                  <p className="card-text">
                    Handle salary calculations, generate pay slips, and manage 
                    compensation packages with our robust payroll system.
                  </p>
                </div>
              </div>
            </div>

            {/* Feature 4 - Analytics Dashboard */}
            <div className="col-lg-6 col-md-6">
              <div className="card h-100 shadow-sm hover-shadow">
                <img 
                  src="/assets/img/AR.jpg" 
                  className="card-img-top feature-image" 
                  alt="Analytics & Reporting"
                  onError={(e) => {
                    e.target.src = "https://via.placeholder.com/400x250/dc3545/ffffff?text=Analytics+Dashboard";
                  }}
                />
                <div className="card-body">
                  <h5 className="card-title text-danger">
                    <i className="fas fa-chart-bar me-2"></i>
                    Analytics & Reporting
                  </h5>
                  <p className="card-text">
                    Get insights into your organization with comprehensive analytics, 
                    reports, and data visualization tools for better decision making.
                  </p>
                </div>
              </div>
            </div>

            {/* Feature 5 - Communication Hub */}
            <div className="col-lg-6 col-md-6">
              <div className="card h-100 shadow-sm hover-shadow">
                <img 
                  src="/assets/img/CH.jpg" 
                  className="card-img-top feature-image" 
                  alt="Communication Hub"
                  onError={(e) => {
                    e.target.src = "https://via.placeholder.com/400x250/6f42c1/ffffff?text=Communication+Hub";
                  }}
                />
                <div className="card-body">
                  <h5 className="card-title text-purple">
                    <i className="fas fa-comments me-2"></i>
                    Communication Hub
                  </h5>
                  <p className="card-text">
                    Foster team collaboration with internal messaging, announcements, 
                    and company-wide communication tools integrated into your workflow.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Call to Action */}
      <section className="py-5 bg-primary text-white">
        <div className="container text-center">
          <h2 className="display-6 fw-bold mb-4">
            Ready to Transform Your HR System?
          </h2>
          <p className="lead mb-4">
            Join thousands of organizations already using HR Sphere to streamline their operations
          </p>
          <Link to="/login" className="btn btn-light btn-lg">
            <i className="fas fa-rocket me-2"></i>
            Start Your Journey Today
          </Link>
        </div>
      </section>

      {/* Footer */}
      <Footer />

      <style jsx>{`
        .bg-gradient-primary {
          background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
        }
        
        .hover-shadow:hover {
          transform: translateY(-5px);
          transition: all 0.3s ease;
          box-shadow: 0 8px 25px rgba(0,0,0,0.15) !important;
        }
        
        .text-purple {
          color: #6f42c1 !important;
        }
        
        .hero-section {
          min-height: 80vh;
          display: flex;
          align-items: center;
          position: relative;
          overflow: hidden;
        }
        
        .hero-text-content {
          position: relative;
          z-index: 2;
          padding: 2rem 0;
        }
        
        .hero-title {
          font-size: clamp(1.8rem, 5vw, 3.5rem);
          line-height: 1.2;
          text-shadow: 0 2px 4px rgba(0,0,0,0.1);
          margin-bottom: 1.5rem !important;
        }
        
        .hero-image-wrapper {
          position: relative;
          z-index: 1;
        }
        
        .hero-image {
          max-width: 100%;
          height: auto;
          border-radius: 15px !important;
          box-shadow: 0 10px 30px rgba(0,0,0,0.2) !important;
        }
        
        .single-line-title {
          white-space: nowrap;
          font-size: clamp(1.2rem, 4vw, 3.5rem);
          line-height: 1.2;
        }
        
        .feature-image {
          height: 250px;
          width: 100%;
          object-fit: cover;
          object-position: center;
          transition: transform 0.3s ease;
        }
        
        .hover-shadow:hover .feature-image {
          transform: scale(1.05);
        }
        
        @media (max-width: 992px) {
          .single-line-title {
            font-size: clamp(1.1rem, 3.5vw, 2.5rem);
          }
        }
        
        @media (max-width: 768px) {
          .hero-section {
            min-height: 60vh;
          }
          
          .single-line-title {
            font-size: clamp(1rem, 3vw, 2rem);
            white-space: normal;
            word-break: break-word;
          }
        }
        
        @media (max-width: 576px) {
          .single-line-title {
            font-size: clamp(0.9rem, 2.5vw, 1.5rem);
            white-space: normal;
            word-break: break-word;
            line-height: 1.1;
          }
        }
      `}</style>
    </div>
  );
}

export default Home;
