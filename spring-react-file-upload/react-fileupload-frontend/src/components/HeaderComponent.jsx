import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import '../hover.css'

class HeaderComponent extends Component {
    render() {
        return (
            <div>
                <nav className='navbar navbar-expand-lg navbar-dark bg-dark'>
                    <Link to='/' className='navbar-brand mx-3'>Image Gallery</Link>
                    <ul className='navbar-nav me-auto mb-2 mb-lg-0'>
                        <li className='nav-item'>
                            <Link className='nav-link active' to='/my-images'>My Images</Link>
                        </li>
                        <li className="nav-item">
                            <Link className='nav-link' to='/upload'>Upload</Link>
                        </li>
                    </ul>
                </nav>
            </div>
        );
    }
}

export default HeaderComponent;