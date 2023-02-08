 window.addEventListener('DOMContentLoaded', () => {
    

    
    

    const uploadResults = document.querySelector('#uploadResults');
    
    document.querySelector('#btnModalUpload').addEventListener('click', e =>{
        console.log('btnModal 누름')
        
        const formData = new FormData();
        const fileInput =  document.querySelector('input[name="files"]');
        console.log(fileInput.files);
        
        Array.from(fileInput.files).forEach(f => {
            formData.append('files', f);
            
        })
        
        
        uploadFiles(formData);
        
        
    })
    

    function uploadFiles(formData){
        
        console.log('컨트롤러로 보냄 upload')
        
        axios.post('/market/api/upload', formData)
            .then(getUploaded)
            .catch(err => { console.log(err)})
        
        
    }
    
        function getUploaded(response) {
        if (response.status == 200) {
            response.data.forEach(x => {
                //console.log(data);
                // 이미지 파일이 아닌 경우, 디폴트 썸네일 이미지를 사용하도록.
                let img = '';
                    img = `<img src="/market/api/view/${x.link}" id="plusImg" data-src="${x.uuid + '_' + x.fileName}" class="card-img-bottom"/>`;

                const htmlStr = `
                                <div class="card" style="width:300px; margin: 5px;">
                                    <div class="card-header d-flex justify-content-center">
                                        ${x.fileName}
                                        <button class="btnImgDelete btn-close" aria-label="Close"
                                            data-uuid="${x.uuid}" data-fname="${x.fileName}"></button>
                                    </div>
                                    <div class="card-body">
                                        ${img}
                                    </div>
                                </div>`;
                
                uploadResults.innerHTML += htmlStr;
                
            });
            
            
            
            document.querySelectorAll('.btnImgDelete').forEach(btn => {
                btn.addEventListener('click', removeFileFromServer);
            });
            
        }
    }
    
    function removeFileFromServer(event){
        event.preventDefault();
        
        const btn = event.target;
        console.log('눈물나,,')
        console.log(btn)
        const uuid = btn.getAttribute('data-uuid');
       const fname = btn.getAttribute('data-fname');      
        const fileName = uuid+'_'+fname;  
        console.log(uuid);
        console.log(fname);
        
        axios.delete('/market/api/view/'+fileName)
            .then(btn.closest('.card').remove())
    }


})