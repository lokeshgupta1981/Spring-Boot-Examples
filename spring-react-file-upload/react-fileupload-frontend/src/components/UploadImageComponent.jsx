import React, { Component } from 'react';
import { Navigate } from 'react-router-dom';
import FileService from '../services/FileService';

class UploadImageComponent extends Component {
    constructor() {
        super();

        this.state = {
            file: File,
            fileUploaded: null
        }
    }

    onFileChange = (event) => {
        this.setState({
            file: event.target.files[0]
        });
    }

    onUpload = () => {
        const formData = new FormData();

        formData.append('file', this.state.file);

        FileService.uploadImage(formData).then((response) => {
            console.log(response.data);
            this.setState({ fileUploaded: true });
        }).catch(error => {
            console.log(error);
        });
    }

    render() {
        return (
            <div className='row'>
                {this.state.fileUploaded && (
                    <Navigate to="/my-images" replace={true} />
                )}

                <div className='card col-md-6 offset-md-3 mt-5'>
                    <h3 className='text-center'>Upload Image</h3>
                    <div className='card-body'>
                        <label>Select a file:</label>
                        <input className='mx-2' type='file' name='file' onChange={this.onFileChange}></input>
                        <button className='btn btn-success btn-sm' disabled={!this.state.file} onClick={this.onUpload}>Upload</button>
                    </div>
                </div>
            </div >
        );
    }
}

export default UploadImageComponent;