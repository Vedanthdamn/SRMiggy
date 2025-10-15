import { useState, useEffect } from 'react';
import { walletAPI } from '../utils/api';

const Wallet = () => {
  const [balance, setBalance] = useState(0);
  const [transactions, setTransactions] = useState([]);
  const [amount, setAmount] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadWalletData();
  }, []);

  const loadWalletData = async () => {
    try {
      const [balanceResponse, transactionsResponse] = await Promise.all([
        walletAPI.getBalance(),
        walletAPI.getTransactions(),
      ]);
      setBalance(balanceResponse.data);
      setTransactions(transactionsResponse.data);
    } catch (err) {
      console.error('Error loading wallet data:', err);
      setError('Failed to load wallet data');
    }
  };

  const handleAddMoney = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    const amountValue = parseFloat(amount);
    if (isNaN(amountValue) || amountValue <= 0) {
      setError('Please enter a valid amount');
      return;
    }

    setLoading(true);

    try {
      const response = await walletAPI.addMoney(amountValue);
      setBalance(response.data.balance);
      setSuccess(response.data.message);
      setAmount('');
      await loadWalletData(); // Reload to get updated transactions
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add money');
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString('en-IN', {
      dateStyle: 'medium',
      timeStyle: 'short',
    });
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">My Wallet</h1>

        {/* Balance Card */}
        <div className="bg-gradient-to-r from-primary to-purple-600 rounded-lg shadow-lg p-8 mb-8 text-white">
          <div className="flex justify-between items-center">
            <div>
              <p className="text-sm opacity-90 mb-2">Available Balance</p>
              <p className="text-4xl font-bold">₹{balance.toFixed(2)}</p>
            </div>
            <div className="text-6xl opacity-30">💰</div>
          </div>
        </div>

        {/* Add Money Section */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Add Money</h2>
          
          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
              {error}
            </div>
          )}
          
          {success && (
            <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
              {success}
            </div>
          )}

          <form onSubmit={handleAddMoney} className="flex gap-4">
            <div className="flex-1">
              <input
                type="number"
                step="0.01"
                min="1"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                placeholder="Enter amount"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-primary focus:border-primary"
                required
              />
            </div>
            <button
              type="submit"
              disabled={loading}
              className="bg-primary text-white px-6 py-2 rounded-lg hover:bg-opacity-90 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Processing...' : 'Add Money'}
            </button>
          </form>

          {/* Quick Amount Buttons */}
          <div className="mt-4 flex gap-2">
            <button
              onClick={() => setAmount('100')}
              className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200"
            >
              ₹100
            </button>
            <button
              onClick={() => setAmount('500')}
              className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200"
            >
              ₹500
            </button>
            <button
              onClick={() => setAmount('1000')}
              className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200"
            >
              ₹1000
            </button>
            <button
              onClick={() => setAmount('2000')}
              className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200"
            >
              ₹2000
            </button>
          </div>
        </div>

        {/* Transaction History */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Transaction History</h2>
          
          {transactions.length === 0 ? (
            <p className="text-gray-500 text-center py-8">No transactions yet</p>
          ) : (
            <div className="space-y-4">
              {transactions.map((transaction) => (
                <div
                  key={transaction.id}
                  className="flex justify-between items-center border-b border-gray-200 pb-4 last:border-b-0"
                >
                  <div className="flex-1">
                    <p className="font-medium text-gray-900">
                      {transaction.description}
                    </p>
                    <p className="text-sm text-gray-500">
                      {formatDate(transaction.createdAt)}
                    </p>
                  </div>
                  <div className="text-right">
                    <p
                      className={`text-lg font-semibold ${
                        transaction.type === 'CREDIT'
                          ? 'text-green-600'
                          : 'text-red-600'
                      }`}
                    >
                      {transaction.type === 'CREDIT' ? '+' : '-'}₹
                      {transaction.amount.toFixed(2)}
                    </p>
                    <p className="text-sm text-gray-500">
                      Balance: ₹{transaction.balanceAfter.toFixed(2)}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Wallet;
