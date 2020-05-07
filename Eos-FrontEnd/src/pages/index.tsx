import React from 'react';
import styles from './index.less';
import { Button, PageHeader, Input } from 'antd';
import 'antd/dist/antd.css';

export default () => {
    return (
        <div>
            <div style={{ zIndex: 999, position: 'absolute', width: '100vw' }}>
                <div>
                    <PageHeader
                        title="Eos Docer Registry"
                        extra={[
                        <Input.Search size="large" style={{width: 400}} />
                        ]}
                    />
                </div>
            </div>
            <div className={styles.eosBanner}>
                <div className={styles.eosBannerTitle}>
                    <div>
                        <h1>Eos Docker Registry</h1>
                    </div>
                </div>
            </div>
            <div className={styles.eosContent}>
                <div>
                    <Button ghost shape="round" size="large">Manage Repo</Button>
                </div>
            </div>
        </div>
    );
}
