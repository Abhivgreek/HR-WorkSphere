import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { employeeAPI } from './services/api';
import { departmentDesignations, showSuccessMessage, showErrorMessage } from './utils/utils';

function EditRecord() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const employeeId = searchParams.get('id');
  
  const [form, setForm] = useState({
    employeeName: '',
    email: '',
    gender: 'M',
    dateOfBirth: '',
    joinDate: '',
    contactNumber: '',
    aadhaarNumber: '',
    accountNumber: '',
    department: '',
    designation: '',
    previousCompany: '',
    pfNumber: '',
    salary: '',
    address: '',
    permanentAddress: '',
    role: 'USER',
    active: true,
  });
  
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);
  const [designations, setDesignations] = useState([]);
  
  const departments = Object.keys(departmentDesignations);

  // Fetch employee data on component mount
  useEffect(() => {
    if (employeeId) {
      fetchEmployeeData();
    } else {
      setInitialLoading(false);
      showErrorMessage('Error', 'No employee ID provided');
    }
  }, [employeeId]);
  
  // Update designations when department changes
  useEffect(() => {
    if (form.department) {
      setDesignations(departmentDesignations[form.department] || []);
    }
  }, [form.department]);
  
  const fetchEmployeeData = async () => {
    try {
      setInitialLoading(true);
      const employee = await employeeAPI.getById(employeeId);
      console.log('Fetched employee data:', employee); // Debug log
      
      // Map API response to form structure - handle EmployeeDTO structure
      const formData = {
        employeeName: employee.employeeName || '',
        email: employee.email || '',
        gender: employee.gender || 'M',
        dateOfBirth: employee.dateOfBirth || '',
        joinDate: employee.joinDate || '',
        contactNumber: employee.mobileNumber || employee.contactNumber || '',
        aadhaarNumber: employee.aadhaarNumber || '',
        accountNumber: employee.accountNumber || '',
        department: employee.department || '',
        designation: employee.designation || '',
        previousCompany: employee.previousCompany || '',
        pfNumber: employee.pfNumber || '',
        salary: employee.salary ? employee.salary.toString() : '',
        address: employee.address && employee.address !== 'na' && employee.address !== 'N/A' ? employee.address : '',
        permanentAddress: employee.permanentAddress && employee.permanentAddress !== 'na' && employee.permanentAddress !== 'N/A' ? employee.permanentAddress : '',
        role: employee.role || 'USER',
        active: employee.active !== false,
      };
      
      console.log('Mapped form data:', formData); // Debug log
      setForm(formData);
    } catch (error) {
      console.error('Error fetching employee:', error);
      showErrorMessage('Error', 'Failed to fetch employee data');
    } finally {
      setInitialLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    const newValue = type === 'checkbox' ? checked : value;
    
    setForm({
      ...form,
      [name]: newValue,
    });
    
    // Reset designation when department changes
    if (name === 'department') {
      setForm(prev => ({ ...prev, designation: '' }));
    }
  };

  const validateForm = () => {
    const errors = [];
    
    if (!form.employeeName.trim()) errors.push('Employee name is required');
    if (!form.email.trim()) errors.push('Email is required');
    if (!form.contactNumber.trim()) errors.push('Mobile number is required');
    if (!form.department) errors.push('Department is required');
    if (!form.designation) errors.push('Designation is required');
    if (!form.salary || parseFloat(form.salary) <= 0) errors.push('Valid salary is required');
    if (!form.address.trim()) errors.push('Current address is required');
    
    return errors;
  };

  const handleSubmit = async (e, shouldNavigate = false) => {
    e.preventDefault();
    
    // Validate form before submitting
    const validationErrors = validateForm();
    if (validationErrors.length > 0) {
      showErrorMessage('Validation Error', validationErrors.join('\n'));
      return;
    }
    
    setLoading(true);
    
    try {
      // Map form data to EmployeeDTO expected format
      const employeeData = {
        employeeName: form.employeeName.trim(),
        email: form.email.trim(),
        gender: form.gender,
        dateOfBirth: form.dateOfBirth,
        joinDate: form.joinDate,
        mobileNumber: form.contactNumber.trim(), // DTO expects mobileNumber
        department: form.department,
        designation: form.designation,
        salary: parseFloat(form.salary) || 0,
        address: form.address.trim(), // Don't default to N/A, use actual address
        role: form.role,
        aadhaarNumber: form.aadhaarNumber.trim(),
        accountNumber: form.accountNumber.trim(),
        previousCompany: form.previousCompany.trim(),
        pfNumber: form.pfNumber.trim(),
        permanentAddress: form.permanentAddress.trim() || form.address.trim(), // Use current address if permanent is empty
        active: form.active
      };
      
      console.log('Sending employee data:', employeeData); // Debug log
      const response = await employeeAPI.update(employeeId, employeeData);
      console.log('Update response:', response); // Debug log
      
      // Check if the response indicates success
      const isSuccess = response && (response.success === true || response.success === undefined);
      
      if (isSuccess) {
        showSuccessMessage('Success!', 'Employee updated successfully');
        
        if (shouldNavigate) {
          // Navigate back to employee list after successful update
          setTimeout(() => {
            navigate('/all-employee');
          }, 1500); // Increased delay to ensure message is seen
        } else {
          // Keep the current form data instead of refetching to avoid reset
          console.log('Employee updated successfully, keeping current form state');
        }
      } else {
        // Handle cases where the response indicates failure
        const errorMessage = response?.message || 'Failed to update employee';
        showErrorMessage('Update Failed', errorMessage);
      }
    } catch (error) {
      console.error('Error updating employee:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to update employee';
      showErrorMessage('Error!', errorMessage);
    } finally {
      setLoading(false);
    }
  };

  // Handle save and return
  const handleSaveAndReturn = (e) => {
    handleSubmit(e, true);
  };

  // Show loading spinner while initial data is being fetched
  if (initialLoading) {
    return (
      <section>
        <div className="row">
          <div className="col-md-10 offset-md-1">
            <div className="card mt-3">
              <div className="card-body text-center">
                <div className="spinner-border text-primary" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
                <p className="mt-2">Loading employee data...</p>
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
        <div className="col-md-10 offset-md-1">
          <div className="card mt-3">
            <div className="card-body">
              <h4 style={{ textAlign: 'center' }}>Update Employee Registration Form</h4>
              <hr />
              <form onSubmit={handleSubmit}>
                <div className="row">
                  {/* Employee Name */}
                  <div className="col-md-3 mt-2">
                    <label htmlFor="employeeName" className="form-label">Employee Name</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <input className="form-control" type="text" name="employeeName" id="employeeName" autoComplete="off" required value={form.employeeName} onChange={handleChange} />
                  </div>
                  {/* Email */}
                  <div className="col-md-3 mt-2">
                    <label htmlFor="email" className="form-label">Email</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <input className="form-control" type="email" name="email" id="email" autoComplete="off" required value={form.email} onChange={handleChange} />
                  </div>
                  {/* Gender */}
                  <div className="col-md-3 mt-2">
                    <label className="form-label">Gender</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <input className="form-check-input" type="radio" name="gender" id="genderM" value="M" checked={form.gender === 'M'} onChange={handleChange} required /> Male
                    <input className="form-check-input ms-5" type="radio" name="gender" id="genderF" value="F" checked={form.gender === 'F'} onChange={handleChange} required /> Female
                  </div>
                  {/* Date of Birth */}
                  <div className="col-md-3 mt-2">
                    <label htmlFor="dateOfBirth" className="form-label">Date of Birth</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <input className="form-control" type="date" name="dateOfBirth" id="dateOfBirth" autoComplete="off" required value={form.dateOfBirth} onChange={handleChange} />
                  </div>
                  {/* Joining Date */}
                  <div className="col-md-3 mt-2">
                    <label htmlFor="joinDate" className="form-label">Joining Date</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <input className="form-control" type="date" name="joinDate" id="joinDate" autoComplete="off" required value={form.joinDate} onChange={handleChange} />
                  </div>
                  {/* Mobile Number */}
                  <div className="col-md-3 mt-2">
                    <label htmlFor="contactNumber" className="form-label">Mobile Number</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <input className="form-control" type="tel" name="contactNumber" id="contactNumber" autoComplete="off" required value={form.contactNumber} onChange={handleChange} />
                  </div>
                  {/* Aadhaar Number */}
                  <div className="col-md-3 mt-2">
                    <label htmlFor="aadhaarNumber" className="form-label">Aadhaar Number</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <input className="form-control" type="number" name="aadhaarNumber" id="aadhaarNumber" autoComplete="off" required value={form.aadhaarNumber} onChange={handleChange} />
                  </div>
                  {/* Account Number */}
                  <div className="col-md-3 mt-2">
                    <label htmlFor="accountNumber" className="form-label">Account Number</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <input className="form-control" type="text" name="accountNumber" id="accountNumber" autoComplete="off" required value={form.accountNumber} onChange={handleChange} />
                  </div>
                  {/* Department */}
                  <div className="col-md-3 mt-2">
                    <label htmlFor="department" className="form-label">Department</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <select className="form-select" id="department" name="department" value={form.department} onChange={handleChange}>
                      <option value="">Select Department</option>
                      <option value="Development">Development</option>
                      <option value="QA & Automation Testing">QA & Automation Testing</option>
                      <option value="Networking">Networking</option>
                      <option value="HR Team">HR Team</option>
                      <option value="Security">Security</option>
                      <option value="Sales & Marketing">Sales & Marketing</option>
                    </select>
                  </div>
                  {/* Designation */}
                  <div className="col-md-3 mt-2">
                    <label htmlFor="designation" className="form-label">Designation</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <select className="form-select" id="designation" name="designation" value={form.designation} onChange={handleChange} disabled={!form.department}>
                      <option value="">Select Designation</option>
                      {designations.map(design => (
                        <option key={design} value={design}>{design}</option>
                      ))}
                    </select>
                  </div>
                  {/* Previous Company Name */}
                  <div className="col-md-3 mt-2">
                    <label htmlFor="previousCompany" className="form-label">Previous Company Name</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <input className="form-control" type="text" name="previousCompany" id="previousCompany" autoComplete="off" required value={form.previousCompany} onChange={handleChange} />
                  </div>
                  {/* PF Number */}
                  <div className="col-md-3 mt-2">
                    <label htmlFor="pfNumber" className="form-label">PF Number</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <input className="form-control" type="text" name="pfNumber" id="pfNumber" autoComplete="off" required value={form.pfNumber} onChange={handleChange} />
                  </div>
                  {/* Salary */}
                  <div className="col-md-3 mt-2">
                    <label htmlFor="salary" className="form-label">Salary</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <input className="form-control" type="number" name="salary" id="salary" autoComplete="off" required value={form.salary} onChange={handleChange} />
                  </div>
                  {/* Current Address */}
                  <div className="col-md-3 mt-2">
                    <label htmlFor="address" className="form-label">Current Address</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <textarea name="address" id="address" className="form-control" rows="3" required value={form.address} onChange={handleChange}></textarea>
                  </div>
                  {/* Permanent Address */}
                  <div className="col-md-3 mt-2">
                    <label htmlFor="permanentAddress" className="form-label">Permanent Address</label>
                  </div>
                  <div className="col-md-9 mt-2">
                    <textarea name="permanentAddress" id="permanentAddress" className="form-control" rows="3" required value={form.permanentAddress} onChange={handleChange}></textarea>
                  </div>
                  {/* Active */}
                  <div className="col-md-3 mt-2">
                    <label className="form-check-label" htmlFor="active">Active</label>
                  </div>
                  <div className="col-md-9 mt-2 form-check form-switch">
                    <input style={{ fontSize: 25 }} className="form-check-input ms-1" type="checkbox" name="active" id="active" checked={form.active} onChange={handleChange} />
                  </div>
                  {/* Submit */}
                  <div className="container text-center mt-3">
                    <div className="d-flex justify-content-between">
                      <button type="button" className="btn btn-secondary" onClick={() => navigate('/all-employee')}>
                        <i className="fas fa-arrow-left me-2"></i>
                        Back to List
                      </button>
                      <div className="d-flex gap-2">
                        <button type="submit" className="btn btn-success" disabled={loading || initialLoading}>
                          {loading ? (
                            <>
                              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                              Updating...
                            </>
                          ) : (
                            <>
                              <i className="fas fa-save me-2"></i>
                              Save Changes
                            </>
                          )}
                        </button>
                        <button type="button" className="btn btn-primary" onClick={handleSaveAndReturn} disabled={loading || initialLoading}>
                          {loading ? (
                            <>
                              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                              Saving...
                            </>
                          ) : (
                            <>
                              <i className="fas fa-check-double me-2"></i>
                              Save & Return
                            </>
                          )}
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

export default EditRecord; 