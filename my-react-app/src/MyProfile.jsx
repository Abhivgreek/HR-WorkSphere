import React, { useState, useEffect } from 'react';
import { employeeAPI } from './services/api';
import { showErrorMessage } from './utils/utils';

function MyProfile() {
  const [employee, setEmployee] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const fetchUserProfile = async (isRefresh = false) => {
    try {
      if (isRefresh) {
        setRefreshing(true);
      }
      
      // Get user data from localStorage
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      
      if (userData.id) {
        // Fetch detailed employee data from API
        const employeeData = await employeeAPI.getById(userData.id);
        console.log('Fetched employee data for profile:', employeeData); // Debug log
        
        setEmployee({
          employeeName: employeeData.employeeName || employeeData.name || userData.name || 'Not specified',
          designation: employeeData.designation || userData.designation || 'Not specified',
          email: employeeData.email || userData.email || 'Not specified',
          dateOfBirth: employeeData.dateOfBirth || 'Not specified',
          mobileNumber: employeeData.mobileNumber || employeeData.contactNumber || employeeData.contact || 'Not specified',
          department: employeeData.department || 'Not specified',
          currentAddress: (employeeData.address && employeeData.address !== 'na' && employeeData.address !== 'N/A') ? employeeData.address : 'Not specified',
          permanentAddress: (employeeData.permanentAddress && employeeData.permanentAddress !== 'na' && employeeData.permanentAddress !== 'N/A') ? employeeData.permanentAddress : ((employeeData.address && employeeData.address !== 'na' && employeeData.address !== 'N/A') ? employeeData.address : 'Not specified'),
          salary: employeeData.salary || 'Not specified',
          joinDate: employeeData.joinDate || 'Not specified'
        });
      } else {
        // Fallback to localStorage data
        setEmployee({
          employeeName: userData.name || 'User',
          designation: userData.designation || 'Employee',
          email: userData.email || 'Not specified',
          dateOfBirth: 'Not specified',
          mobileNumber: 'Not specified',
          department: 'Not specified',
          currentAddress: 'Not specified',
          permanentAddress: 'Not specified',
          salary: 'Not specified',
          joinDate: 'Not specified'
        });
      }
    } catch (error) {
      console.error('Error fetching profile:', error);
      showErrorMessage('Error', 'Failed to load profile data');
      
      // Fallback to localStorage data
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      setEmployee({
        employeeName: userData.name || 'User',
        designation: userData.designation || 'Employee',
        email: userData.email || 'Not specified',
        dateOfBirth: 'Not specified',
        mobileNumber: 'Not specified',
        department: 'Not specified',
        currentAddress: 'Not specified',
        permanentAddress: 'Not specified',
        salary: 'Not specified',
        joinDate: 'Not specified'
      });
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  const handleRefresh = () => {
    fetchUserProfile(true);
  };

  useEffect(() => {
    fetchUserProfile();
  }, []);

  if (loading) {
    return (
      <section>
        <div className="row">
          <div className="col-md-6 offset-md-3 mt-3">
            <div className="card">
              <div className="card-body text-center">
                <div className="spinner-border" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
                <p className="mt-2">Loading profile...</p>
              </div>
            </div>
          </div>
        </div>
      </section>
    );
  }

  if (!employee) {
    return (
      <section>
        <div className="row">
          <div className="col-md-6 offset-md-3 mt-3">
            <div className="card">
              <div className="card-body text-center">
                <p>Unable to load profile data.</p>
              </div>
            </div>
          </div>
        </div>
      </section>
    );
  }

  return (
    <section>
      <div className="row">
        <div className="col-md-6 offset-md-3 mt-3">
          <div className="card">
          <div className="card-body">
              <div className="d-flex justify-content-end mb-2">
                <button 
                  className="btn btn-outline-primary btn-sm" 
                  onClick={handleRefresh} 
                  disabled={refreshing}
                >
                  {refreshing ? (
                    <>
                      <span className="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
                      Refreshing...
                    </>
                  ) : (
                    <>
                      <i className="fas fa-sync-alt me-1"></i>
                      Refresh
                    </>
                  )}
                </button>
              </div>
              <div className="container text-center">
                <img alt="image" src="/assets/img/default.png" style={{ height: 100 }} />
                <h5>{employee.employeeName}</h5>
                <p><i>{employee.designation}</i></p>
                <hr />
              </div>
              <table className="table">
                <tbody>
                  <tr>
                    <th scope="row">1</th>
                    <td><b>Email</b></td>
                    <td>{employee.email}</td>
                  </tr>
                  <tr>
                    <th scope="row">2</th>
                    <td><b>Date of Birth </b></td>
                    <td>{employee.dateOfBirth}</td>
                  </tr>
                  <tr>
                    <th scope="row">3</th>
                    <td><b>Mobile Number </b></td>
                    <td>{employee.mobileNumber}</td>
                  </tr>
                  <tr>
                    <th scope="row">4</th>
                    <td><b>Department</b></td>
                    <td>{employee.department}</td>
                  </tr>
                  <tr>
                    <th scope="row">5</th>
                    <td><b>Current Address</b></td>
                    <td>{employee.currentAddress}</td>
                  </tr>
                  <tr>
                    <th scope="row">6</th>
                    <td><b>Permanent Address</b></td>
                    <td>{employee.permanentAddress}</td>
                  </tr>
                </tbody>
              </table>
             
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

export default MyProfile; 